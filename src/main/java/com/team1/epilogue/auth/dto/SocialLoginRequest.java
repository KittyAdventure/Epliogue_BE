package com.team1.epilogue.auth.dto;

import lombok.Data;

/**
 * [클래스 레벨]
 * 소셜 로그인(구글, 카카오) 요청 데이터를 전달하기 위한 DTO
 */
@Data
public class SocialLoginRequest {
    private String provider;
    private String token;
}
