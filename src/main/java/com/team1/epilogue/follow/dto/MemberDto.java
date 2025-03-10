package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MemberDto {
    private String id;
    private String loginId;
    private String nickname;
    private String profileUrl;
}
