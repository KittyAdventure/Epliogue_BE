package com.team1.epilogue.rating;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.rating.dto.RatingRequestDto;
import com.team1.epilogue.rating.dto.RatingResponseDto;
import com.team1.epilogue.rating.entity.Rating;
import com.team1.epilogue.rating.exception.RatingNotFoundException;
import com.team1.epilogue.rating.repository.RatingRepository;
import com.team1.epilogue.rating.service.RatingService;
import com.team1.epilogue.review.exception.BookNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class RatingServiceTest {

    @InjectMocks
    private RatingService ratingService;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private BookRepository bookRepository;

    private Member member;
    private CustomMemberDetails memberDetails;
    private Book book;
    private RatingRequestDto ratingRequestDto;
    private Rating existingRating;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = new Member(); // 테스트용 Member 객체 생성
        book = new Book(); // 테스트용 Book 객체 생성
        ratingRequestDto = new RatingRequestDto(4.5); // 테스트용 RatingRequestDto 객체 생성

        existingRating = new Rating(1L, book, member, 2.0);


        // CustomMemberDetails 생성 (Member 기반)
        memberDetails = new CustomMemberDetails(
                member.getId(),
                member.getLoginId(),
                member.getPassword(),
                Collections.emptyList(),  // 예: 권한 목록
                member.getName(),
                member.getEmail()
        );
    }

    /**
     * 별점 생성 테스트
     * bookId로 책을 찾고, 별점을 4.5로 저장한 후, 해당 별점이 제대로 반환되는지 확인합니다
     */
    @Test
    void createRating() {
        // given
        when(bookRepository.findById("bookId")).thenReturn(java.util.Optional.of(book));
        when(ratingRepository.save(argThat(rating -> rating.getScore().equals(4.5))))
                .thenReturn(new Rating(1L, book, member, 4.5));

        // when
        RatingResponseDto response = ratingService.createRating("bookId", ratingRequestDto, memberDetails);

        // then
        assertNotNull(response);
        assertEquals(4.5, response.getScore());
        verify(ratingRepository, times(1)).save(argThat(rating -> rating.getScore().equals(4.5)));
    }

    /**
     * 별점 수정 테스트
     * 기존 별점(2.0)을 4.5로 업데이트한 후, 업데이트된 별점이 정상적으로 반환되는지 확인합니다
     */
    @Test
    void updateRating() {
        // given
        when(ratingRepository.findByMemberIdAndBookId(member.getId(), "bookId"))
                .thenReturn(Optional.of(existingRating));

        // when
        RatingResponseDto response = ratingService.updateRating("bookId", ratingRequestDto, memberDetails);

        // then
        assertNotNull(response);
        assertEquals(4.5, response.getScore());

        assertEquals(4.5, existingRating.getScore());
    }

    /**
     * 별점 삭제 테스트
     */
    @Test
    void deleteRating() {
        // given
        Rating existingRating = new Rating(1L, book, member, 3.0);
        when(ratingRepository.findByMemberIdAndBookId(member.getId(), "bookId")).thenReturn(java.util.Optional.of(existingRating));

        // when
        ratingService.deleteRating("bookId", memberDetails);

        // then
        verify(ratingRepository, times(1)).delete(existingRating);
    }

    /**
     * 책이 존재하지 않는 경우 별점 생성 시 예외 발생 테스트
     */
    @Test
    void createRating_BookNotFound() {
        // given
        when(bookRepository.findById("bookId")).thenReturn(java.util.Optional.empty());

        // when, then
        assertThrows(BookNotFoundException.class, () -> ratingService.createRating("bookId", ratingRequestDto, memberDetails));
    }

    /**
     * 별점이 존재하지 않는 경우 별점 수정 시 예외 발생 테스트
     */
    @Test
    void updateRating_RatingNotFound() {
        // given
        when(ratingRepository.findByMemberIdAndBookId(member.getId(), "bookId")).thenReturn(java.util.Optional.empty());

        // when, then
        assertThrows(RatingNotFoundException.class, () -> ratingService.updateRating("bookId", ratingRequestDto, memberDetails));
    }

    /**
     * 별점이 존재하지 않는 경우 별점 삭제 시 예외 발생 테스트
     */
    @Test
    void deleteRating_RatingNotFound() {
        // given
        when(ratingRepository.findByMemberIdAndBookId(member.getId(), "bookId")).thenReturn(java.util.Optional.empty());

        // when, then
        assertThrows(RatingNotFoundException.class, () -> ratingService.deleteRating("bookId", memberDetails));
    }
}
