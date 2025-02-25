package com.team1.epilogue.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * [클래스 레벨]
 * Member 엔티티는 회원 정보를 데이터베이스에 저장하기 위한 JPA 엔티티
 * 새로운 DB 스키마에 따라 필드명이 변경되었으며,
 * 로그인 ID, 닉네임, 이름, 생년월일, 이메일, 전화번호, 프로필 URL, 포인트, 소셜 정보, 가입일 등의 컬럼을 포함
 */
@Entity
@Table(name = "member")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    /**
     * [필드 레벨]
     * id: 회원의 고유 식별자로, 데이터베이스에서 자동 생성
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * [필드 레벨]
     * loginId: 회원의 로그인 ID로, 고유하며 필수 값
     */
    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    /**
     * [필드 레벨]
     * password: 회원의 비밀번호를 암호화하여 저장
     */
    @Column(nullable = false)
    private String password;

    /**
     * [필드 레벨]
     * nickname: 회원의 닉네임이며, 필수 값
     */
    @Column(nullable = false)
    private String nickname;

    /**
     * [필드 레벨]
     * name: 회원의 이름, 필수 값
     */
    @Column(nullable = false)
    private String name;

    /**
     * [필드 레벨]
     * birthDate: 회원의 생년월일. ISO 형식(LocalDate)으로 저장
     */
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    /**
     * [필드 레벨]
     * email: 회원의 이메일 주소로, 고유하며 필수 값
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * [필드 레벨]
     * phone: 회원의 전화번호, 필수 값
     */
    @Column(nullable = false)
    private String phone;

    /**
     * [필드 레벨]
     * profileUrl: 회원의 프로필 사진 S3 URL.
     */
    @Column(name = "profile_url")
    private String profileUrl;

    /**
     * [필드 레벨]
     * point: 회원의 포인트, 기본값 0
     */
    @Column(nullable = false)
    private int point;

    /**
     * [필드 레벨]
     * social: 소셜 로그인 정보(예: KAKAO, GOOGLE 등), 선택 값
     */
    @Column
    private String social;

    /**
     * [필드 레벨]
     * createdAt: 회원 가입일(생성일)로, 현재 시간이 할당
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
