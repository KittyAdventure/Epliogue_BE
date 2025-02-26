package com.team1.epilogue.review.dto;

import com.team1.epilogue.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 클라이언트에게 전달할 리뷰 상세 정보를 담는 DTO입니다
 */
@Getter
@Builder
public class ReviewResponseDto {
    private Long id;
    private String content;
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    /**
     * Review 엔티티를 DTO로 변환합니다
     *
     * @param review 변환할 Review 엔티티
     * @return 변환된 ReviewResponseDto 객체
     */
    public static ReviewResponseDto of(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .content(review.getContent())
                .nickname(review.getMember().getNickname())
                .createdAt(review.getCreatedAt())
                .modifiedAt(review.getModifiedAt())
                .build();
    }
}
