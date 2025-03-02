package com.team1.epilogue.auth.dto;

import lombok.Builder;
import lombok.Data;

/**
 * [클래스 레벨]
 * LoginResponse 클래스는 로그인 성공 시 반환되는 응답 데이터를 담는 DTO
 */
@Data
@Builder
public class LoginResponse {
    /**
     * [필드 레벨]
     * message: 로그인 결과 메시지
     */
    private String message;

    /**
     * [필드 레벨]
     * accessToken: 발급된 JWT 액세스 토큰
     */
    private String accessToken;

    /**
     * [필드 레벨]
     * user: 로그인한 사용자 정보
     */
    private UserInfo user;

    /**
     * [클래스 레벨]
     * UserInfo 클래스는 로그인한 사용자의 정보를 담는 내부 DTO
     */
    @Data
    @Builder
    public static class UserInfo {
        /**
         * [필드 레벨]
         * id: 사용자 고유 식별자
         */
        private String id;

        /**
         * [필드 레벨]
         * userId: 사용자 로그인 ID
         */
        private String userId;

        /**
         * [필드 레벨]
         * name: 사용자 이름
         */
        private String name;

        /**
         * [필드 레벨]
         * profileImg: 사용자 프로필 이미지 URL
         */
        private String profileImg;
    }
}
