
package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

/**
 * [클래스 레벨]
 * 여러 개의 리뷰 정보를 포함하는 DTO.
 *
 * 필드:
 * - review: 리뷰 목록 (ReviewDto 객체 리스트)
 * - pagination: 페이지네이션 정보 (PaginationDto 객체)
 */
@Data
@AllArgsConstructor
public class ReviewListResponse {
    private List<ReviewDto> review;
    private PaginationDto pagination;
}