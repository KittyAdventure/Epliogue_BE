package com.team1.epilogue.auth.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserInfo {

    private Long id;
    private KakaoAccount kakao_account;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoAccount {
        private String email;
        private KakaoProfile profile;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoProfile {

        private String nickname;
        private String profileImageUrl;
    }
}
