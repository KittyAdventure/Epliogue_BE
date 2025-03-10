package com.team1.epilogue.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Builder
public class MemberResponse {
    private String id;
    private String loginId;
    private String nickname;
    private String name;
    private String birthDate;
    private String email;
    private String phone;
    private String profileUrl;
}
