package com.team1.epilogue.share.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoShareResponse {
    private String status;
    private String shareUrl;
    private String message;
}
