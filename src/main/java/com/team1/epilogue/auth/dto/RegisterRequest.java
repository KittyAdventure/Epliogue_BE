package com.team1.epilogue.authfix.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * [클래스 레벨]
 * RegisterRequest 클래스는 사용자 등록 시 클라이언트로부터 전달받은 데이터를 담는 DTO
 * 새 스키마에 따라 필드명이 loginId, birthDate, profileUrl 등으로 변경되었으며, nickname 필드도 포함
 */
@Data
public class RegisterRequest {

    /**
     * [필드 레벨]
     * loginId: 사용자 로그인 ID, 필수 입력
     */
    @NotBlank(message = "loginId는 필수입니다.")
    private String loginId;

    /**
     * [필드 레벨]
     * password: 사용자 비밀번호, 필수 입력, 최소 6자 이상
     */
    @NotBlank(message = "password는 필수입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;

    /**
     * [필드 레벨]
     * nickname: 사용자 닉네임, 필수 입력
     */
    @NotBlank(message = "nickname은 필수입니다.")
    private String nickname;

    /**
     * [필드 레벨]
     * name: 사용자 이름, 필수 입력
     */
    @NotBlank(message = "name은 필수입니다.")
    private String name;

    /**
     * [필드 레벨]
     * birthDate: 사용자 생년월일, 필수 입력, 형식은 "YYYY-MM-DD"이어야 함
     */
    @NotBlank(message = "birthDate는 필수입니다.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "생년월일 형식은 YYYY-MM-DD 여야 합니다.")
    private String birthDate;

    /**
     * [필드 레벨]
     * email: 사용자 이메일, 필수 입력, 유효한 이메일 형식
     */
    @NotBlank(message = "email은 필수입니다.")
    @Email(message = "유효한 이메일 형식을 입력하세요.")
    private String email;

    /**
     * [필드 레벨]
     * phone: 사용자 전화번호, 필수 입력, 형식 조건 적용
     */
    @NotBlank(message = "phone은 필수입니다.")
    @Pattern(regexp = "\\d{2,3}-\\d{3,4}-\\d{4}", message = "전화번호 형식에 맞지 않습니다.")
    private String phone;

    /**
     * [필드 레벨]
     * profileUrl: 사용자 프로필 사진 URL, 선택 입력
     */
    private String profileUrl;
}
