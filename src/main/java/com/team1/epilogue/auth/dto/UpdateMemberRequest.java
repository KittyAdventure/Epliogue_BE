package com.team1.epilogue.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class UpdateMemberRequest {
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식을 입력하세요.")
    private String email;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "\\d{2,3}-\\d{3,4}-\\d{4}", message = "전화번호 형식에 맞지 않습니다.")
    private String phone;

    // profilePhoto는 선택적 입력
    private String profilePhoto;
}
