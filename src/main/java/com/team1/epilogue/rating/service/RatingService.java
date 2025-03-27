package com.team1.epilogue.rating.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
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
  private final MemberRepository memberRepository;

  @Transactional
  public RatingResponseDto createRating(
      String bookId,
      RatingRequestDto ratingRequestDto,
      CustomMemberDetails memberDetails
  ) {
    Member member = getMemberOrThrow(memberDetails.getId());
    Book book = getBookOrThrow(bookId);

    if (ratingRepository.findByMemberIdAndBookId(member.getId(), bookId).isPresent()) {
      throw new IllegalArgumentException("이미 별점을 남겼습니다. 수정하려면 PUT 요청을 사용하세요");
    }

    Rating rating = ratingRequestDto.toEntity(book, member);
    Rating savedRating = ratingRepository.save(rating);

    updateBookRating(bookId);

    return RatingResponseDto.from(savedRating);
  }

  @Transactional
  public RatingResponseDto updateRating(
      String bookId,
      RatingRequestDto ratingRequestDto,
      CustomMemberDetails memberDetails
  ) {
    Member member = getMemberOrThrow(memberDetails.getId());
    Rating rating = getRatingOrThrow(member.getId(), bookId);

    rating.updateScore(ratingRequestDto.getScore());
    updateBookRating(bookId);

    return RatingResponseDto.from(rating);
  }

  @Transactional
  public void deleteRating(String bookId, CustomMemberDetails memberDetails) {
    Member member = getMemberOrThrow(memberDetails.getId());
    Rating rating = getRatingOrThrow(member.getId(), bookId);

    ratingRepository.delete(rating);
    updateBookRating(bookId);
  }

  @Transactional
  public void updateBookRating(String bookId) {
    Double avgRating = ratingRepository.findAverageRatingByBookId(bookId);
    Book book = bookRepository.findByIdWithLock(bookId)
        .orElseThrow(() -> new BookNotFoundException("책을 찾을 수 없습니다."));

    book.updateAvgRating(avgRating != null ? avgRating : 0.0);
    bookRepository.save(book);
  }

  // === 헬퍼 메서드 ===

  private Member getMemberOrThrow(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException("ID가 " + memberId + "인 회원을 찾을 수 없습니다."));
  }

  private Book getBookOrThrow(String bookId) {
    return bookRepository.findById(bookId)
        .orElseThrow(() -> new BookNotFoundException("존재하지 않는 책입니다."));
  }

  private Rating getRatingOrThrow(Long memberId, String bookId) {
    return ratingRepository.findByMemberIdAndBookId(memberId, bookId)
        .orElseThrow(() -> new RatingNotFoundException("해당 책에 대한 별점이 존재하지 않습니다."));
  }
}
