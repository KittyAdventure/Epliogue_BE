package com.team1.epilogue.auth.dto;

import lombok.Data;
import lombok.Builder;


/**
 * [클래스 레벨]
 * 사용자 정보를 클라이언트에 전달하기 위한 DTO
 */
@Data
@Builder
public class UserDto {
    private Long id;
    private String userId;
    private String name;
    private String profileImg;
}
