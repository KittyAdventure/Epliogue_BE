package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewDto {
    private String id;
    private String bookId;
    private String content;
    private String imageUrl;
    private String createdAt;
    private MemberDto member;
}
