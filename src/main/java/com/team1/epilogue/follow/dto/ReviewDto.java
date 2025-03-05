package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewDto {
    private String id;
    private String bookId;
    private String content;
    private String imageUrl;
    private String createdAt;
    private MemberDto member;
}
