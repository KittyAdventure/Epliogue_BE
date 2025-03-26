package com.team1.epilogue.review.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.security.JwtTokenProvider;
import com.team1.epilogue.auth.service.S3Service;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.follow.entity.Follow;
import com.team1.epilogue.follow.repository.FollowRepository;
import com.team1.epilogue.review.dto.ReviewRequestDto;
import com.team1.epilogue.review.dto.ReviewResponseDto;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.review.entity.ReviewLike;
import com.team1.epilogue.review.exception.AlreadyLikedException;
import com.team1.epilogue.review.exception.BookNotFoundException;
import com.team1.epilogue.review.exception.LikeNotFoundException;
import com.team1.epilogue.review.exception.ReviewNotFoundException;
import com.team1.epilogue.review.exception.UnauthorizedReviewAccessException;
import com.team1.epilogue.review.repository.ReviewLikeRepository;
import com.team1.epilogue.review.repository.ReviewRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final BookRepository bookRepository;
  private final ReviewLikeRepository reviewLikeRepository;
  private final MemberRepository memberRepository;
  private final FollowRepository followRepository;
  private final S3Service s3Service;
  private final JwtTokenProvider jwtTokenProvider;


  @Transactional
  public ReviewResponseDto createReview(
      String bookId,
      ReviewRequestDto reviewRequestDto,
      List<MultipartFile> images,
      CustomMemberDetails memberDetails
  ) {
    Member member = getMemberOrThrow(memberDetails.getId());
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new BookNotFoundException("존재하지 않는 책입니다."));

    if (images != null && images.size() > 5) {
      throw new IllegalArgumentException("최대 5개의 이미지만 업로드할 수 있습니다.");
    }

    List<String> imageUrls = uploadImages(images);

    Review review = reviewRequestDto.toEntity(book, member, imageUrls);
    reviewRepository.save(review);

    return ReviewResponseDto.from(review);
  }

  public Page<ReviewResponseDto> getReviews(
      String bookId,
      int page,
      int size,
      String sortType,
      String token
  ) {
    Pageable pageable = createPageable(page, size, sortType);
    Page<Review> reviews = reviewRepository.findByBookIdWithMember(bookId, pageable);

    Long memberId = null;

    // 토큰이 있을때만 사용자 ID 추출
    if (token != null && token.startsWith("Bearer ")) {
      try {
        String pureToken = token.substring(7);
        String memberIdStr = jwtTokenProvider.getMemberIdFromJWT(pureToken);
        memberId = Long.parseLong(memberIdStr);
      } catch (Exception e) {
        memberId = null;
      }
    }

    Map<Long, Boolean> likedMap = new HashMap<>();

    if (memberId != null) {
      // 현재 페이지의 리뷰 ID 목록 수집
      List<Long> reviewIds = reviews.getContent().stream()
          .map(Review::getId)
          .collect(Collectors.toList());

      List<Long> likedReviewIds = reviewLikeRepository.findLikedReviewIdsByMemberId(memberId,
          reviewIds);
      likedMap = likedReviewIds.stream()
          .collect(Collectors.toMap(id -> id, id -> true)); // 빠른 조회를 위한 Map 변환
    }

    Map<Long, Boolean> finalLikedMap = likedMap;

    return reviews.map(review -> {
      ReviewResponseDto dto = ReviewResponseDto.from(review);

      // Map을 활용해 좋아요 여부 설정
      dto.setLiked(finalLikedMap.getOrDefault(review.getId(), false));
      return dto;
    });
  }

  public ReviewResponseDto getReviewDetail(Long reviewId, String token) {
    Review review = reviewRepository.findByIdWithBookAndMember(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

    ReviewResponseDto dto = ReviewResponseDto.from(review);

    if (token != null && token.startsWith("Bearer ")) {
      try {
        String pureToken = token.substring(7);
        Long memberId = Long.parseLong(jwtTokenProvider.getMemberIdFromJWT(pureToken));
        boolean liked = reviewLikeRepository.existsByReviewIdAndMemberId(review.getId(), memberId);
        dto.setLiked(liked);
      } catch (Exception e) {
        dto.setLiked(false);
      }
    } else {
      dto.setLiked(false);
    }

    return dto;
  }

  public Page<ReviewResponseDto> getLatestReviews(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Review> reviews = reviewRepository.findAllReviewsSortedByLatest(pageable);
    return reviews.map(ReviewResponseDto::from);
  }

  @Transactional
  public ReviewResponseDto updateReview(
      Long reviewId,
      ReviewRequestDto reviewRequestDto,
      List<MultipartFile> images,
      CustomMemberDetails memberDetails
  ) {
    Member member = getMemberOrThrow(memberDetails.getId());
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

    if (!review.getMember().getId().equals(member.getId())) {
      throw new UnauthorizedReviewAccessException("리뷰 작성자만 수정할 수 있습니다.");
    }

    review.updateReview(reviewRequestDto.getContent());

    List<String> existingImageUrls = new ArrayList<>(review.getImageUrls());
    List<String> updatedImageUrls = new ArrayList<>();

    // 기존 이미지 유지 여부 확인 (기본값: 빈 리스트)
    List<String> imageUrlsToKeep =
        (reviewRequestDto.getImageUrls() != null) ? reviewRequestDto.getImageUrls()
            : new ArrayList<>();

    for (String imageUrl : existingImageUrls) {
      if (imageUrlsToKeep.contains(imageUrl)) { // 기존 이미지 유지 확인
        updatedImageUrls.add(imageUrl);
      } else {
        s3Service.deleteFile(imageUrl); // 삭제할 이미지는 S3에서도 삭제
      }
    }

    // 새로운 이미지 업로드
    if (images != null && !images.isEmpty()) {
      if (updatedImageUrls.size() + images.size() > 5) {
        throw new IllegalArgumentException("최대 5개의 이미지만 업로드할 수 있습니다.");
      }
      updatedImageUrls.addAll(uploadImages(images));
    }

    // 항상 업데이트 (이미지 변경 여부와 상관없이)
    review.updateImageUrls(updatedImageUrls);

    return ReviewResponseDto.from(review);
  }

  public void deleteReview(Long reviewId, CustomMemberDetails memberDetails) {
    Member member = getMemberOrThrow(memberDetails.getId());
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

    if (!review.getMember().getId().equals(member.getId())) {
      throw new UnauthorizedReviewAccessException("리뷰 작성자만 삭제할 수 있습니다.");
    }

    reviewRepository.delete(review);
  }

  @Transactional
  public void likeReview(Long reviewId, CustomMemberDetails memberDetails) {
    Member member = getMemberOrThrow(memberDetails.getId());
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

    if (reviewLikeRepository.existsByReviewIdAndMemberId(reviewId, member.getId())) {
      throw new AlreadyLikedException("이미 좋아요를 눌렀습니다.");
    }

    ReviewLike reviewLike = new ReviewLike(review, member);
    reviewLikeRepository.save(reviewLike);

    reviewRepository.increaseLikeCount(reviewId);
  }

  @Transactional
  public void unlikeReview(Long reviewId, CustomMemberDetails memberDetails) {
    Member member = getMemberOrThrow(memberDetails.getId());
    ReviewLike reviewLike = reviewLikeRepository.findByReviewIdAndMemberId(reviewId, member.getId())
        .orElseThrow(() -> new LikeNotFoundException("취소할 좋아요가 없습니다."));

    reviewLikeRepository.delete(reviewLike);

    reviewRepository.decreaseLikeCount(reviewId);
  }

  public Page<ReviewResponseDto> getFriendsReviews(
      String bookId,
      CustomMemberDetails memberDetails,
      int page,
      int size,
      String sortType
  ) {
    Member currentMember = getMemberOrThrow(memberDetails.getId());
    List<Follow> followings = followRepository.findByFollowerWithFollowed(currentMember);
    List<Member> friendMembers = followings.stream()
        .map(Follow::getFollowed)
        .collect(Collectors.toList());

    if (friendMembers.isEmpty()) {
      return Page.empty();
    }
    Pageable pageable = createPageable(page, size, sortType);
    Page<Review> reviews = reviewRepository.findByBookIdAndMemberInWithFetchJoin(bookId,
        friendMembers, pageable);
    return reviews.map(ReviewResponseDto::from);
  }

  // === 헬퍼 메서드 ===

  private Member getMemberOrThrow(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException("ID가 " + memberId + "인 회원을 찾을 수 없습니다."));
  }

  private List<String> uploadImages(List<MultipartFile> images) {
    List<String> imageUrls = new ArrayList<>();
    if (images != null && !images.isEmpty()) {
      for (MultipartFile image : images) {
        if (image != null && !image.isEmpty()) {
          imageUrls.add(s3Service.uploadFile(image));
        }
      }
    }
    return imageUrls;
  }

  private Pageable createPageable(int page, int size, String sortType) {
    Sort sort = sortType.equals("likes")
        ? Sort.by(Sort.Direction.DESC, "likeCount").and(Sort.by(Sort.Direction.DESC, "createdAt"))
        : Sort.by(Sort.Direction.DESC, "createdAt");

    return PageRequest.of(page - 1, size, sort);
  }
}
