package com.team1.epilogue.review;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.review.dto.ReviewRequestDto;
import com.team1.epilogue.review.dto.ReviewResponseDto;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.review.exception.ReviewNotFoundException;
import com.team1.epilogue.review.exception.UnauthorizedReviewAccessException;
import com.team1.epilogue.review.repository.ReviewRepository;
import com.team1.epilogue.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Member testMember;
    private Book testBook;
    private Review testReview;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .id(1L)
                .nickname("tester")
                .build();

        testBook = Book.builder()
                .id("1111111111111")
                .title("테스트 책")
                .build();

        testReview = Review.builder()
                .id(1L)
                .content("테스트책 리뷰입니다.")
                .book(testBook)
                .member(testMember)
                .build();
    }

    /**
     * 리뷰 생성 테스트
     */
    @Test
    void createReview_Success() {
        // given
        ReviewRequestDto requestDto = new ReviewRequestDto("정말 좋은 책입니다.");
        when(bookRepository.findById(testBook.getId())).thenReturn(Optional.of(testBook));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // when
        ReviewResponseDto response = reviewService.createReview(testBook.getId(), requestDto, testMember);

        // then
        assertThat(response.getContent()).isEqualTo("정말 좋은 책입니다.");
    }

    /**
     * 특정 책의 리뷰 목록 조회 (페이징) 테스트
     */
    @Test
    void getReviews_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = new PageImpl<>(List.of(testReview));
        when(reviewRepository.findByBookId(testBook.getId(), pageable)).thenReturn(reviewPage);

        // when
        Page<ReviewResponseDto> response = reviewService.getReviews(testBook.getId(), pageable);

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getContent()).isEqualTo("테스트책 리뷰입니다.");
    }

    /**
     * 특정 리뷰 상세 조회 테스트
     */
    @Test
    void getReview_Success() {
        // given
        when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));

        // when
        ReviewResponseDto response = reviewService.getReview(testReview.getId());

        // then
        assertThat(response.getId()).isEqualTo(testReview.getId());
        assertThat(response.getContent()).isEqualTo("테스트책 리뷰입니다.");
    }

    /**
     * 존재하지 않는 리뷰 조회 시 예외 발생 테스트
     */
    @Test
    void getReview_Throws_ReviewNotFoundException() {
        // given
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(ReviewNotFoundException.class, () -> reviewService.getReview(999L));
    }

    /**
     * 리뷰 수정 테스트 (성공)
     */
    @Test
    void updateReview_Success() {
        // given
        ReviewRequestDto updateDto = new ReviewRequestDto("수정된 리뷰입니다.");
        when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));

        // when
        ReviewResponseDto response = reviewService.updateReview(testReview.getId(), updateDto, testMember);

        // then
        assertThat(response.getContent()).isEqualTo("수정된 리뷰입니다.");
    }

    /**
     * 리뷰 수정 테스트 (작성자가 아닐 때 예외 발생)
     */
    @Test
    void updateReview_Throws_UnauthorizedReviewAccessException() {
        // given
        Member anotherMember = Member.builder().id(2L).nickname("다른 사용자").build();
        ReviewRequestDto updateDto = new ReviewRequestDto("수정된 리뷰입니다.");
        when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));

        // when & then
        assertThrows(UnauthorizedReviewAccessException.class, () -> reviewService.updateReview(testReview.getId(), updateDto, anotherMember));
    }

    /**
     * 리뷰 삭제 테스트 (성공)
     */
    @Test
    void deleteReview_Success() {
        // given
        when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));
        doNothing().when(reviewRepository).delete(testReview);

        // when
        reviewService.deleteReview(testReview.getId(), testMember);

        // then
        verify(reviewRepository, times(1)).delete(testReview);
    }

    /**
     * 리뷰 삭제 테스트 (작성자가 아닐 때 예외 발생)
     */
    @Test
    void deleteReview_Throws_UnauthorizedReviewAccessException() {
        // given
        Member anotherMember = Member.builder().id(2L).nickname("다른 사용자").build();
        when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));

        // when & then
        assertThrows(UnauthorizedReviewAccessException.class, () -> reviewService.deleteReview(testReview.getId(), anotherMember));
    }
}
