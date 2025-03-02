package com.team1.epilogue.auth.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class LoginResponse {
    private String message;
    private String accessToken;
    private UserInfo user;


    @Data
    @Builder
    public static class UserInfo {
        private String id;
        private String userId;
        private String name;
        private String profileImg;
    }
}
