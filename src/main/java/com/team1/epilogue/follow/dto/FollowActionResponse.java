package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FollowActionResponse {
    private String message;
    private String followerLoginId;
    private String followedLoginId;
}
