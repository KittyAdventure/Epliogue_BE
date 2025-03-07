package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * [클래스 레벨]
 * 페이지네이션 정보를 담는 DTO.
 *
 * 필드:
 * - page: 현재 페이지 번호
 * - limit: 한 페이지에 보여줄 항목 개수
 * - total: 전체 항목 개수
 */
@Data
@AllArgsConstructor
public class PaginationDto {
    private int page;
    private int limit;
    private long total;
}
