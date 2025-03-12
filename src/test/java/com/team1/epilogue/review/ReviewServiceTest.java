package com.team1.epilogue.review;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.review.dto.ReviewRequestDto;
import com.team1.epilogue.review.dto.ReviewResponseDto;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.review.entity.ReviewLike;
import com.team1.epilogue.review.exception.AlreadyLikedException;
import com.team1.epilogue.review.exception.LikeNotFoundException;
import com.team1.epilogue.review.exception.ReviewNotFoundException;
import com.team1.epilogue.review.exception.UnauthorizedReviewAccessException;
import com.team1.epilogue.review.repository.ReviewLikeRepository;
import com.team1.epilogue.review.repository.ReviewRepository;
import com.team1.epilogue.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Member testMember;
    private CustomMemberDetails testMemberDetails;
    private Book testBook;
    private Review testReview;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .id(1L)
                .loginId("testUser")
                .password("testPass")
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

        testMemberDetails = new CustomMemberDetails(
                testMember,
                testMember.getId(),
                testMember.getLoginId(),
                testMember.getPassword(),
                Collections.emptyList(),
                testMember.getNickname(),
                null
        );
    }

    @Test
    @DisplayName("리뷰 생성")
    void createReview_Success() {
        // given
        ReviewRequestDto requestDto = new ReviewRequestDto("정말 좋은 책입니다.");
        when(bookRepository.findById(testBook.getId())).thenReturn(Optional.of(testBook));
        when(reviewRepository.save(argThat(review -> review.getContent().equals("정말 좋은 책입니다."))))
                .thenReturn(testReview);

        // when
        ReviewResponseDto response = reviewService.createReview(testBook.getId(), requestDto, testMemberDetails);

        // then
        assertThat(response.getContent()).isEqualTo("정말 좋은 책입니다.");
    }

    @Test
    @DisplayName("특정 책의 리뷰 목록 조회 (페이징)")
    void getReviews_Success() {
        // given
        String sortType = "likes";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "likeCount")
                .and(Sort.by(Sort.Direction.DESC, "createdAt")));
        Page<Review> reviewPage = new PageImpl<>(List.of(testReview));
        when(reviewRepository.findByBookIdWithMember(testBook.getId(), pageable)).thenReturn(reviewPage);

        // when
        Page<ReviewResponseDto> response = reviewService.getReviews(testBook.getId(), 1, 10, sortType);

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getContent()).isEqualTo("테스트책 리뷰입니다.");
    }

    @Test
    @DisplayName("특정 리뷰 상세 조회")
    void getReviewDetail_Success() {
        // given
        when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));

        // when
        ReviewResponseDto response = reviewService.getReviewDetail(testReview.getId());

        // then
        assertThat(response.getId()).isEqualTo(testReview.getId());
        assertThat(response.getContent()).isEqualTo("테스트책 리뷰입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 조회 시 예외 발생")
    void getReviewDetail_Throws_ReviewNotFoundException() {
        // given
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(ReviewNotFoundException.class, () -> reviewService.getReviewDetail(999L));
    }

    @Test
    @DisplayName("리뷰 수정")
    void updateReview_Success() {
        // given
        ReviewRequestDto updateDto = new ReviewRequestDto("수정된 리뷰입니다.");
        when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));

        // when
        ReviewResponseDto response = reviewService.updateReview(testReview.getId(), updateDto, testMemberDetails);

        // then
        assertThat(response.getContent()).isEqualTo("수정된 리뷰입니다.");
    }

    @Test
    @DisplayName("리뷰 수정 - 작성자가 아닐 때 예외 발생")
    void updateReview_Throws_UnauthorizedReviewAccessException() {
        // given
        Member anotherMember = Member.builder().id(2L).nickname("다른 사용자").build();
        CustomMemberDetails anotherMemberDetails = new CustomMemberDetails(
                anotherMember, anotherMember.getId(), "otherUser", "password", Collections.emptyList(), "다른 사용자", null
        );

        ReviewRequestDto updateDto = new ReviewRequestDto("수정된 리뷰입니다.");
        when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));

        // when & then
        assertThrows(UnauthorizedReviewAccessException.class, () -> reviewService.updateReview(testReview.getId(), updateDto, anotherMemberDetails));
    }

    @Test
    @DisplayName("리뷰 삭제")
    void deleteReview_Success() {
        // given
        when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));
        doNothing().when(reviewRepository).delete(testReview);

        // when
        reviewService.deleteReview(testReview.getId(), testMemberDetails);

        // then
        verify(reviewRepository).delete(testReview);
    }

    @Test
    @DisplayName("리뷰 삭제 - 작성자가 아닐 때 예외 발생")
    void deleteReview_Throws_UnauthorizedReviewAccessException() {
        // given
        Member anotherMember = Member.builder().id(2L).nickname("다른 사용자").build();
        CustomMemberDetails anotherMemberDetails = new CustomMemberDetails(
                anotherMember, anotherMember.getId(), "otherUser", "password", Collections.emptyList(), "다른 사용자", null
        );

        when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));

        // when & then
        assertThrows(UnauthorizedReviewAccessException.class, () -> reviewService.deleteReview(testReview.getId(), anotherMemberDetails));
    }

    @Test
    @DisplayName("리뷰 좋아요")
    void likeReview_success() {
        // given
        when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));
        when(reviewLikeRepository.existsByReviewIdAndMemberId(testReview.getId(), testMember.getId())).thenReturn(false);

        ArgumentCaptor<ReviewLike> reviewLikeCaptor = ArgumentCaptor.forClass(ReviewLike.class);
        ArgumentCaptor<Long> reviewIdCaptor = ArgumentCaptor.forClass(Long.class);

        // when
        assertDoesNotThrow(() -> reviewService.likeReview(testReview.getId(), testMemberDetails));

        // then
        verify(reviewLikeRepository).save(reviewLikeCaptor.capture());
        verify(reviewRepository).increaseLikeCount(reviewIdCaptor.capture());

        assertThat(reviewLikeCaptor.getValue().getMember().getId()).isEqualTo(testMember.getId());
        assertThat(reviewLikeCaptor.getValue().getReview().getId()).isEqualTo(testReview.getId());
        assertThat(reviewIdCaptor.getValue()).isEqualTo(testReview.getId());
    }

    @Test
    @DisplayName("리뷰 좋아요 - 이미 좋아요한 경우 예외 발생")
    void likeReview_fail_AlreadyLikedException() {
        // given
        when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));
        when(reviewLikeRepository.existsByReviewIdAndMemberId(testReview.getId(), testMember.getId()))
                .thenReturn(true); // 이미 좋아요를 누른 경우

        // when & then
        assertThrows(AlreadyLikedException.class, () -> reviewService.likeReview(testReview.getId(), testMemberDetails));

        // 좋아요가 추가되지 않아야 함
        verify(reviewLikeRepository, never()).save(any(ReviewLike.class));
        verify(reviewRepository, never()).increaseLikeCount(anyLong());
    }

    @Test
    @DisplayName("리뷰 좋아요 취소")
    void unlikeReview_success() {
        // given
        ReviewLike reviewLike = new ReviewLike(testReview, testMember);

        when(reviewLikeRepository.findByReviewIdAndMemberId(testReview.getId(), testMember.getId()))
                .thenReturn(Optional.of(reviewLike));

        ArgumentCaptor<ReviewLike> reviewLikeCaptor = ArgumentCaptor.forClass(ReviewLike.class);
        ArgumentCaptor<Long> reviewIdCaptor = ArgumentCaptor.forClass(Long.class);

        // when
        assertDoesNotThrow(() -> reviewService.unlikeReview(testReview.getId(), testMemberDetails));

        // then
        verify(reviewLikeRepository).delete(reviewLikeCaptor.capture());
        verify(reviewRepository).decreaseLikeCount(reviewIdCaptor.capture());

        assertThat(reviewLikeCaptor.getValue().getReview().getId()).isEqualTo(testReview.getId());
        assertThat(reviewLikeCaptor.getValue().getMember().getId()).isEqualTo(testMember.getId());
        assertThat(reviewIdCaptor.getValue()).isEqualTo(testReview.getId());
    }

    @Test
    @DisplayName("리뷰 좋아요 취소 - 좋아요한 적 없는 경우 예외 발생")
    void unlikeReview_fail_LikeNotFoundException() {
        // given
        when(reviewLikeRepository.findByReviewIdAndMemberId(testReview.getId(), testMember.getId()))
                .thenReturn(Optional.empty()); // 좋아요한 적 없음

        // when & then
        assertThrows(LikeNotFoundException.class, () -> reviewService.unlikeReview(testReview.getId(), testMemberDetails));

        // 좋아요 삭제나 감소가 일어나지 않아야 함
        verify(reviewLikeRepository, never()).delete(any(ReviewLike.class));
        verify(reviewRepository, never()).decreaseLikeCount(anyLong());
    }
}
