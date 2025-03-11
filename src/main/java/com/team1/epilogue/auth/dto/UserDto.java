package com.team1.epilogue.auth.dto;

import lombok.*;


@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UserDto {
    private Long id;
    private String userId;
    private String name;
    private String profileImg;
}
