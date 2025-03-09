package com.team1.epilogue.review.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
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

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    /**
     * 책에 대한 리뷰를 생성합니다
     *
     * @param bookId           리뷰가 작성될 책의 ID
     * @param reviewRequestDto 클라이언트가 전달한 리뷰 데이터
     * @param member           리뷰 작성자
     * @return 생성된 리뷰의 상세 정보를 담은 DTO
     */
    @Transactional
    public ReviewResponseDto createReview(String bookId, ReviewRequestDto reviewRequestDto, Member member) {
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

    /**
     * 특정 리뷰의 상세 정보를 조회합니다
     *
     * @param reviewId 조회할 리뷰의 ID
     * @return 해당 리뷰의 상세 정보를 담은 DTO
     */
    public ReviewResponseDto getReviewDetail(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

        return ReviewResponseDto.from(review);
    }

    /**
     * 리뷰를 수정합니다
     *
     * @param reviewId         수정할 리뷰의 ID
     * @param reviewRequestDto 수정된 리뷰 데이터를 담은 DTO
     * @param member           수정 요청을 보낸 사용자
     * @return 수정된 리뷰의 상세 정보를 담은 DTO
     */
    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto reviewRequestDto, Member member) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

        if (!review.getMember().getId().equals(member.getId())) {
            throw new UnauthorizedReviewAccessException("리뷰 작성자만 수정할 수 있습니다.");
        }

        review.updateReview(reviewRequestDto.getContent());
        return ReviewResponseDto.from(review);
    }

    /**
     * 리뷰를 삭제합니다
     *
     * @param reviewId 삭제할 리뷰의 ID
     * @param member   삭제 요청을 보낸 사용자
     */
    public void deleteReview(Long reviewId, Member member) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

        if (!review.getMember().getId().equals(member.getId())) {
            throw new UnauthorizedReviewAccessException("리뷰 작성자만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }

    // 좋아요
    @Transactional
    public void likeReview(Long reviewId, Member member) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

        if (reviewLikeRepository.existsByReviewIdAndMemberId(reviewId, member.getId())) {
            throw new AlreadyLikedException("이미 좋아요를 눌렀습니다.");
        }

        ReviewLike reviewLike = new ReviewLike(review, member);
        reviewLikeRepository.save(reviewLike);

        reviewRepository.increaseLikeCount(reviewId);
    }

    // 좋아요 삭제
    @Transactional
    public void unlikeReview(Long reviewId, Member member) {
        ReviewLike reviewLike = reviewLikeRepository.findByReviewIdAndMemberId(reviewId, member.getId())
                .orElseThrow(() -> new LikeNotFoundException("취소할 좋아요가 없습니다."));

        reviewLikeRepository.delete(reviewLike);

        reviewRepository.decreaseLikeCount(reviewId);
    }
}
