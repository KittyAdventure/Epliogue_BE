package com.team1.epilogue.auth.dto;

import lombok.Builder;
import lombok.Data;

/**
 * [클래스 레벨]
 * MemberResponse 클래스는 회원 등록 후 반환되는 응답 데이터를 담는 DTO
 */
@Data
@Builder
public class MemberResponse {
    private String id;
    private String loginId;
    private String nickname;
    private String name;
    private String birthDate;
    private String email;
    private String phone;
    private String profileUrl;
}
