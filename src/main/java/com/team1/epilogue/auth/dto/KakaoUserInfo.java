package com.team1.epilogue.auth.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class KakaoUserInfo {
    private Long id;
    private KakaoAccount kakao_account;

    @Data
    public static class KakaoAccount {
        private String email;
        private KakaoProfile profile;
    }

    @Data
    public static class KakaoProfile {
        private String nickname;
        private String profileImageUrl;

    }
}
