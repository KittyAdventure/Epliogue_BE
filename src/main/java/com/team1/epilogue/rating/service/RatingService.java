package com.team1.epilogue.rating.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.security.CustomMemberDetails;
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

    public RatingResponseDto createRating(String bookId, RatingRequestDto ratingRequestDto, CustomMemberDetails memberDetails) {
        Member member = memberDetails.getMember();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("존재하지 않는 책입니다."));

        Rating rating = ratingRequestDto.toEntity(book, member);
        Rating savedRating = ratingRepository.save(rating);

        return RatingResponseDto.from(savedRating);
    }

    @Transactional
    public RatingResponseDto updateRating(String bookId, RatingRequestDto ratingRequestDto, CustomMemberDetails memberDetails) {
        Member member = memberDetails.getMember();
        Rating rating = ratingRepository.findByMemberIdAndBookId(member.getId(), bookId)
                .orElseThrow(() -> new RatingNotFoundException("해당 책에 대한 별점이 존재하지 않습니다."));

        rating.updateScore(ratingRequestDto.getScore());
        Rating updatedRating = ratingRepository.save(rating);

        return RatingResponseDto.from(updatedRating);
    }

    @Transactional
    public void deleteRating(String bookId, CustomMemberDetails memberDetails) {
        Member member = memberDetails.getMember();
        Rating rating = ratingRepository.findByMemberIdAndBookId(member.getId(), bookId)
                .orElseThrow(() -> new RatingNotFoundException("해당 책에 대한 별점이 존재하지 않습니다."));

        ratingRepository.delete(rating);
    }
}
