package com.team1.epilogue.auth.dto;

import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * [클래스 레벨]
 * RegisterRequest 클래스는 사용자 등록 시 클라이언트로부터 전달받은 데이터를 담는 DTO
 * 새 스키마에 따라 필드명이 loginId, birthDate, profileUrl 등으로 변경되었으며, nickname 필드도 포함
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "loginId는 필수입니다.")
    private String loginId;

    @NotBlank(message = "password는 필수입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "nickname은 필수입니다.")
    private String nickname;

    @NotBlank(message = "name은 필수입니다.")
    private String name;

    @NotBlank(message = "birthDate는 필수입니다.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "생년월일 형식은 YYYY-MM-DD 여야 합니다.")
    private String birthDate;

    @NotBlank(message = "email은 필수입니다.")
    @Email(message = "유효한 이메일 형식을 입력하세요.")
    private String email;

    @NotBlank(message = "phone은 필수입니다.")
    @Pattern(regexp = "\\d{2,3}-\\d{3,4}-\\d{4}", message = "전화번호 형식에 맞지 않습니다.")
    private String phone;

    private String profileUrl;
}
