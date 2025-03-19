package com.team1.epilogue.review.dto;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * 클라이언트로부터 전달받은 리뷰 생성/수정 데이터를 담는 DTO입니다
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDto {

    private String content;

    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    public Review toEntity(Book book, Member member, List<String> imageUrls) {
        return Review.builder()
                .content(content)
                .book(book)
                .member(member)
                .imageUrls(imageUrls)
                .build();
    }
}
