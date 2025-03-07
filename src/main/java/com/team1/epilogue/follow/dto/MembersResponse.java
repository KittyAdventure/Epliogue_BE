package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

/**
 * [클래스 레벨]
 * 여러 회원 정보를 담아 반환하는 DTO.
 *
 * 필드:
 * - members: 회원 목록 (MemberDto 리스트)
 */
@Data
@AllArgsConstructor
public class MembersResponse {
    private List<MemberDto> members;
}
