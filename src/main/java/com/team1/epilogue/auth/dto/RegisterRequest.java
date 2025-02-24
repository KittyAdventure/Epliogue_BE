package com.team1.epilogue.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 클래스 레벨
 * RegisterRequest 클래스는 사용자 등록 시 클라이언트로부터 전달받은 데이터를 담는 DTO(Data Transfer Object)입니다.
 * 각 필드는 유효성 검증 애너테이션을 통해 필수 입력 여부 및 형식 조건을 확인합니다.
 */
@Data
public class RegisterRequest {

    /**
     * 필드 레벨
     * 사용자 아이디(userId)
     * - 필수 입력: 비어 있으면 안 됩니다.
     */
    @NotBlank(message = "userId는 필수입니다.")
    private String userId;

    /**
     * 필드 레벨
     * 사용자 비밀번호(password)
     * - 필수 입력: 비어 있으면 안 됩니다.
     * - 최소 6자 이상이어야 합니다.
     */
    @NotBlank(message = "password는 필수입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;

    /**
     * 필드 레벨
     * 사용자 닉네임(nickname)
     * - 필수 입력: 비어 있으면 안 됩니다.
     */
    @NotBlank(message = "nickname은 필수입니다.")
    private String nickname;

    /**
     * 필드 레벨
     * 사용자 이름(name)
     * - 필수 입력: 비어 있으면 안 됩니다.
     */
    @NotBlank(message = "name은 필수입니다.")
    private String name;

    /**
     * 필드 레벨
     * 사용자 생년월일(birthdate)
     * - 필수 입력: 비어 있으면 안 됩니다.
     * - 형식은 "YYYY-MM-DD"이어야 합니다.
     */
    @NotBlank(message = "birthdate는 필수입니다.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "생년월일 형식은 YYYY-MM-DD 여야 합니다.")
    private String birthdate;

    /**
     * 필드 레벨
     * 사용자 이메일(email)
     * - 필수 입력: 비어 있으면 안 됩니다.
     * - 유효한 이메일 형식을 입력해야 합니다.
     */
    @NotBlank(message = "email은 필수입니다.")
    @Email(message = "유효한 이메일 형식을 입력하세요.")
    private String email;

    /**
     * 필드 레벨
     * 사용자 전화번호(phone)
     * - 필수 입력: 비어 있으면 안 됩니다.
     * - 형식은 "XX-XXX-XXXX" 또는 "XXX-XXXX-XXXX"이어야 합니다.
     */
    @NotBlank(message = "phone은 필수입니다.")
    @Pattern(regexp = "\\d{2,3}-\\d{3,4}-\\d{4}", message = "전화번호 형식에 맞지 않습니다.")
    private String phone;

    /**
     * 필드 레벨
     * 사용자 프로필 사진 URL(profilePhoto)
     * - 선택 입력: 값이 없을 수 있습니다.
     */
    private String profilePhoto;
}
