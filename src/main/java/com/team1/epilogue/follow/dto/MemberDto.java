package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberDto {
    private String id;
    private String loginId;
    private String nickname;
    private String profileUrl;
}
