package com.team1.epilogue.review.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.follow.entity.Follow;
import com.team1.epilogue.follow.repository.FollowRepository;
import com.team1.epilogue.review.dto.ReviewRequestDto;
import com.team1.epilogue.review.dto.ReviewResponseDto;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.review.entity.ReviewLike;
import com.team1.epilogue.review.exception.*;
import com.team1.epilogue.review.repository.ReviewLikeRepository;
import com.team1.epilogue.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final FollowRepository followRepository;


    @Transactional
    public ReviewResponseDto createReview(String bookId, ReviewRequestDto reviewRequestDto, CustomMemberDetails memberDetails) {
        Member member = memberDetails.getMember();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("존재하지 않는 책입니다."));

        Review review = reviewRequestDto.toEntity(book, member);
        reviewRepository.save(review);

        return ReviewResponseDto.from(review);
    }

    public Page<ReviewResponseDto> getReviews(String bookId, int page, int size, String sortType) {
        Pageable pageable = createPageable(page, size, sortType);
        Page<Review> reviews = reviewRepository.findByBookIdWithMember(bookId, pageable);

        return reviews.map(ReviewResponseDto::from);
    }

    private Pageable createPageable(int page, int size, String sortType) {
        Sort sort = sortType.equals("likes")
                ? Sort.by(Sort.Direction.DESC, "likeCount").and(Sort.by(Sort.Direction.DESC, "createdAt"))
                : Sort.by(Sort.Direction.DESC, "createdAt");

        return PageRequest.of(page - 1, size, sort);
    }

    public ReviewResponseDto getReviewDetail(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

        return ReviewResponseDto.from(review);
    }

    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto reviewRequestDto, CustomMemberDetails memberDetails) {
        Member member = memberDetails.getMember();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

        if (!review.getMember().getId().equals(member.getId())) {
            throw new UnauthorizedReviewAccessException("리뷰 작성자만 수정할 수 있습니다.");
        }

        review.updateReview(reviewRequestDto.getContent());
        return ReviewResponseDto.from(review);
    }

    public void deleteReview(Long reviewId, CustomMemberDetails memberDetails) {
        Member member = memberDetails.getMember();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

        if (!review.getMember().getId().equals(member.getId())) {
            throw new UnauthorizedReviewAccessException("리뷰 작성자만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }

    @Transactional
    public void likeReview(Long reviewId, CustomMemberDetails memberDetails) {
        Member member = memberDetails.getMember();
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
        Member member = memberDetails.getMember();
        ReviewLike reviewLike = reviewLikeRepository.findByReviewIdAndMemberId(reviewId, member.getId())
                .orElseThrow(() -> new LikeNotFoundException("취소할 좋아요가 없습니다."));

        reviewLikeRepository.delete(reviewLike);

        reviewRepository.decreaseLikeCount(reviewId);
    }

    public Page<ReviewResponseDto> getFriendsReviews(String bookId, CustomMemberDetails memberDetails, int page, int size, String sortType) {
        Member currentMember = memberDetails.getMember();
        List<Follow> followings = followRepository.findByFollower(currentMember);
        List<Member> friendMembers = followings.stream()
                .map(Follow::getFollowed)
                .collect(Collectors.toList());
        if (friendMembers.isEmpty()) {
            return Page.empty();
        }
        Pageable pageable = createPageable(page, size, sortType);
        Page<Review> reviews = reviewRepository.findByBookIdAndMemberInWithFetchJoin(bookId, friendMembers, pageable);
        return reviews.map(ReviewResponseDto::from);
    }
}