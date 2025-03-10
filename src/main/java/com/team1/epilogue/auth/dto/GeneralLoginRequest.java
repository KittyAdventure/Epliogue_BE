package com.team1.epilogue.auth.dto;

import lombok.*;

/**
 * [클래스 레벨]
 * 일반 로그인 요청 데이터를 전달하기 위한 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneralLoginRequest {
    private String loginId;
    private String password;
}
