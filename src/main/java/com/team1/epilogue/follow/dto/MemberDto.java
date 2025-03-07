package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * [클래스 레벨]
 * 회원 정보를 담는 DTO.
 *
 * 필드:
 * - id: 회원의 고유 식별자
 * - loginId: 회원의 로그인 ID
 * - nickname: 회원의 닉네임
 * - profileUrl: 회원의 프로필 이미지 URL
 */
@Data
@AllArgsConstructor
public class MemberDto {
    private String id;
    private String loginId;
    private String nickname;
    private String profileUrl;
}
