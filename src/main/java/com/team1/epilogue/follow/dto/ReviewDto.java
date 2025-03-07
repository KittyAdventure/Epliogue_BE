package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * [클래스 레벨]
 * 리뷰 정보를 담는 DTO.
 *
 * 필드:
 * - id: 리뷰의 고유 식별자
 * - bookId: 해당 리뷰가 작성된 책의 고유 ID
 * - content: 리뷰 내용
 * - imageUrl: 리뷰에 포함된 이미지 URL (선택적)
 * - createdAt: 리뷰 작성 날짜 및 시간
 * - member: 리뷰 작성자의 정보 (MemberDto 객체)
 */
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
