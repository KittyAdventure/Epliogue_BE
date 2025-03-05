package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDto {
    private String id;
    private String loginId;
    private String nickname;
    private String profileUrl;
}
