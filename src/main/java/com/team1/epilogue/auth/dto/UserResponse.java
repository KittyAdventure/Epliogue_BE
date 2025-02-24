package com.team1.epilogue.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String id;
    private String userId;
    private String nickname;
    private String name;
    private String birthdate;
    private String email;
    private String phone;
    private String profilePhoto;
}
