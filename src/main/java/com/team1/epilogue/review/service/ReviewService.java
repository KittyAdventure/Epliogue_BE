package com.team1.epilogue.review.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.book.Book;
import com.team1.epilogue.book.BookRepository;
import com.team1.epilogue.review.dto.ReviewRequestDto;
import com.team1.epilogue.review.dto.ReviewResponseDto;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    /**
     * 책에 대한 리뷰를 생성합니다
     *
     * @param bookId           리뷰가 작성될 책의 ID
     * @param reviewRequestDto 클라이언트가 전달한 리뷰 데이터
     * @param member           리뷰 작성자
     * @return 생성된 리뷰의 상세 정보를 담은 DTO
     */
    @Transactional
    public ReviewResponseDto createReview(Long bookId, ReviewRequestDto reviewRequestDto, Member member) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));

        Review review = reviewRequestDto.toEntity(book, member);
        reviewRepository.save(review);

        return ReviewResponseDto.of(review);
    }

    /**
     * 특정 책의 모든 리뷰를 조회합니다
     *
     * @param bookId 리뷰를 조회할 책의 ID
     * @return 해당 책의 리뷰 목록을 담은 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviews(Long bookId) {
        // 아직 정렬순서 안정함
        List<Review> reviews = reviewRepository.findByBookId(bookId);

        return reviews.stream()
                .map(ReviewResponseDto::of)
                .collect(Collectors.toList());
    }

    /**
     * 특정 리뷰의 상세 정보를 조회합니다
     *
     * @param reviewId 조회할 리뷰의 ID
     * @return 해당 리뷰의 상세 정보를 담은 DTO
     */
    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        return ReviewResponseDto.of(review);
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
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        if (!review.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("리뷰 작성자만 수정할 수 있습니다.");
        }

        if (review.getContent().equals(reviewRequestDto.getContent())) {
            return ReviewResponseDto.of(review);
        }

        review.updateReview(reviewRequestDto.getContent());
        return ReviewResponseDto.of(review);
    }

    /**
     * 리뷰를 삭제합니다
     *
     * @param reviewId 삭제할 리뷰의 ID
     * @param member   삭제 요청을 보낸 사용자
     */
    @Transactional
    public void deleteReview(Long reviewId, Member member) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        if (!review.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("리뷰 작성자만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }
}
