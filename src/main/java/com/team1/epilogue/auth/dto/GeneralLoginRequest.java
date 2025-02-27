package com.team1.epilogue.auth.dto;

import lombok.Data;

/**
 * [클래스 레벨]
 * 일반 로그인 요청 데이터를 전달하기 위한 DTO
 */

@Data
public class GeneralLoginRequest {
    private String userId;
    private String password;
}
