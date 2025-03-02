package com.team1.epilogue.rating.dto;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.rating.entity.Rating;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingRequestDto {

    @DecimalMin("0.5")
    @DecimalMax("5")
    private Double score;  // 1~5 사이의 점수를 받습니다

    public Rating toEntity(Book book, Member member) {
        return Rating.builder()
                .score(score)
                .book(book)
                .member(member)
                .build();
    }
}
