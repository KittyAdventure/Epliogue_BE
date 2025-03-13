package com.team1.epilogue.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginRequest {
    private String provider;
    private String accessToken;
}
