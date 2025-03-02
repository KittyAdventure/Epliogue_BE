package com.team1.epilogue.rating.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.rating.dto.RatingRequestDto;
import com.team1.epilogue.rating.dto.RatingResponseDto;
import com.team1.epilogue.rating.entity.Rating;
import com.team1.epilogue.rating.exception.RatingNotFoundException;
import com.team1.epilogue.rating.repository.RatingRepository;
import com.team1.epilogue.review.exception.BookNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final BookRepository bookRepository;

    /**
     * 책에 대한 별점을 생성합니다
     *
     * @param bookId           별점을 남길 책의 ID
     * @param ratingRequestDto 클라이언트가 전달한 별점 데이터
     * @param member           현재 인증된 사용자
     * @return 생성된 별점의 상세 정보를 담은 DTO
     */
    public RatingResponseDto createRating(String bookId, RatingRequestDto ratingRequestDto, Member member) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("존재하지 않는 책입니다."));

        Rating rating = ratingRequestDto.toEntity(book, member);
        ratingRepository.save(rating);

        return RatingResponseDto.from(rating);
    }

    /**
     * 책에 대한 별점을 수정합니다
     *
     * @param bookId           별점을 수정할 책의 ID
     * @param ratingRequestDto 수정된 별점 데이터
     * @param member           현재 인증된 사용자
     * @return 수정된 별점의 상세 정보를 담은 DTO
     */
    @Transactional
    public RatingResponseDto updateRating(String bookId, RatingRequestDto ratingRequestDto, Member member) {
        Rating rating = ratingRepository.findByMemberIdAndBookId(member.getId(), bookId)
                .orElseThrow(() -> new RatingNotFoundException("해당 책에 대한 별점이 존재하지 않습니다."));

        rating.updateScore(ratingRequestDto.getScore());
        ratingRepository.save(rating);

        return RatingResponseDto.from(rating);
    }

    /**
     * 책에 대한 별점을 삭제합니다
     *
     * @param bookId 별점을 삭제할 책의 ID
     * @param member 현재 인증된 사용자
     */
    @Transactional
    public void deleteRating(String bookId, Member member) {
        Rating rating = ratingRepository.findByMemberIdAndBookId(member.getId(), bookId)
                .orElseThrow(() -> new RatingNotFoundException("해당 책에 대한 별점이 존재하지 않습니다."));

        ratingRepository.delete(rating);
    }
}
