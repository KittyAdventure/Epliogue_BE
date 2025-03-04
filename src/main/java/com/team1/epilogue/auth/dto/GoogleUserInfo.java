package com.team1.epilogue.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * [클래스 레벨]
 * GoogleUserInfo 클래스는 Google OAuth2 로그인 시 받아오는 사용자 정보를 담는 DTO
 */
@Data
public class GoogleUserInfo {
    /**
     * [필드 레벨]
     * sub: Google의 고유 사용자 ID
     */
    private String sub;

    /**
     * [필드 레벨]
     * email: 사용자 이메일
     */
    private String email;

    /**
     * [필드 레벨]
     * emailVerified: 이메일 인증 여부
     */
    @JsonProperty("email_verified")
    private String emailVerified;

    /**
     * [필드 레벨]
     * name: 사용자 이름
     */
    private String name;

    /**
     * [필드 레벨]
     * picture: 사용자 프로필 사진 URL
     */
    private String picture;

    private String given_name;
    private String family_name;
    private String locale;
    private String aud;
}
