package com.team1.epilogue.auth.dto;

import lombok.Data;

/**
 * [클래스 레벨]
 * KakaoUserInfo 클래스는 Kakao OAuth2 로그인 시 받아오는 사용자 정보를 담는 DTO
 */
@Data
public class KakaoUserInfo {
    /**
     * [필드 레벨]
     * id: 카카오의 고유 사용자 ID
     */
    private Long id;

    /**
     * [필드 레벨]
     * kakao_account: 카카오 계정 정보
     */
    private KakaoAccount kakao_account;

    /**
     * [클래스 레벨]
     * KakaoAccount 클래스는 카카오에서 반환하는 계정 정보를 담는 내부 클래스
     */
    @Data
    public static class KakaoAccount {
        /**
         * [필드 레벨]
         * email: 사용자 이메일
         */
        private String email;

        /**
         * [필드 레벨]
         * profile: 사용자 프로필 정보
         */
        private KakaoProfile profile;
    }

    /**
     * [클래스 레벨]
     * KakaoProfile 클래스는 카카오에서 반환하는 프로필 정보를 담는 내부 클래스
     */
    @Data
    public static class KakaoProfile {
        /**
         * [필드 레벨]
         * nickname: 사용자 닉네임
         */
        private String nickname;

        /**
         * [필드 레벨]
         * profileImageUrl: 사용자 프로필 사진 URL
         */
        private String profileImageUrl;
    }
}
