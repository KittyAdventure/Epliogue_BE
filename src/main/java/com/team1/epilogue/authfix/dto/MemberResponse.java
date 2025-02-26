package com.team1.epilogue.authfix.dto;

import lombok.Builder;
import lombok.Data;

/**
 * [클래스 레벨]
 * MemberResponse 클래스는 회원 등록 후 반환되는 응답 데이터를 담는 DTO
 * 반환 데이터에는 회원의 고유 ID, 로그인 ID, 닉네임, 이름, 생년월일, 이메일, 전화번호, 프로필 URL 등이 포함
 */
@Data
@Builder
public class MemberResponse {

    /**
     * [필드 레벨]
     * id: 데이터베이스에서 생성된 회원의 고유 식별자. 문자열로 변환하여 반환
     */
    private String id;

    /**
     * [필드 레벨]
     * loginId: 회원의 로그인 ID
     */
    private String loginId;

    /**
     * [필드 레벨]
     * nickname: 회원의 닉네임
     */
    private String nickname;

    /**
     * [필드 레벨]
     * name: 회원의 이름
     */
    private String name;

    /**
     * [필드 레벨]
     * birthDate: 회원의 생년월일
     */
    private String birthDate;

    /**
     * [필드 레벨]
     * email: 회원의 이메일 주소
     */
    private String email;

    /**
     * [필드 레벨]
     * phone: 회원의 전화번호
     */
    private String phone;

    /**
     * [필드 레벨]
     * profileUrl: 회원의 프로필 사진 URL
     */
    private String profileUrl;
}
