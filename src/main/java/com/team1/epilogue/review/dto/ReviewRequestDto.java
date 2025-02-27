package com.team1.epilogue.review.dto;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.book.Book;
import com.team1.epilogue.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 클라이언트로부터 전달받은 리뷰 생성/수정 데이터를 담는 DTO입니다
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDto {

    private String content;

    /**
     * DTO의 데이터를 이용해 Review 엔티티를 생성합니다
     *
     * @param book   리뷰와 연관된 책 엔티티
     * @param member 리뷰 작성자 엔티티
     * @return Review 엔티티
     */
    public Review toEntity(Book book, Member member) {
        return Review.builder()
                .content(content)
                .book(book)
                .member(member)
                .build();
    }
}
