package com.team1.epilogue.share.dto;

import lombok.Getter;

@Getter
public class KakaoShareRequest {
    private String targetType;
    private String targetId;
    private String recipient;
    private String message;
}