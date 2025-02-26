package com.team1.epilogue.authfix.service;

import com.team1.epilogue.authfix.dto.RegisterRequest;
import com.team1.epilogue.authfix.dto.MemberResponse;
import com.team1.epilogue.authfix.exception.IdAlreadyExistException;
import com.team1.epilogue.authfix.exception.EmailNotValidException;
import com.team1.epilogue.authfix.exception.EmailAlreadyExistException;
import com.team1.epilogue.authfix.model.Member;
import com.team1.epilogue.authfix.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {

    // [필드 레벨]
    // memberRepository: 회원 정보를 저장하고 조회하는 Repository.
    private final MemberRepository memberRepository;

    // [필드 레벨]
    // passwordEncoder: 사용자의 비밀번호를 암호화하는 컴포넌트.
    private final PasswordEncoder passwordEncoder;

    // [메서드 레벨]
    // registerMember: 회원 가입 요청(RegisterRequest)을 처리하여 회원 정보를 저장하고 MemberResponse를 반환.
    public MemberResponse registerMember(RegisterRequest request) {
        // [검증 단계] 이미 존재하는 로그인 ID 검사.
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new IdAlreadyExistException();
        }

        // [검증 단계] 이미 존재하는 이메일 검사.
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistException();
        }

        // [검증 단계] 이메일 형식 검사.
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new EmailNotValidException();
        }

        // [처리 단계] RegisterRequest를 Member 엔티티로 변환.
        Member member = Member.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .name(request.getName())
                .birthDate(LocalDate.parse(request.getBirthDate()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .profileUrl(request.getProfileUrl())
                .point(0)
                .social(null)
                .createdAt(LocalDateTime.now())
                .build();

        // [저장 단계] 회원 정보를 데이터베이스에 저장.
        Member savedMember = memberRepository.save(member);

        // [반환 단계] 저장된 정보를 MemberResponse로 변환하여 반환.
        return MemberResponse.builder()
                .id(String.valueOf(savedMember.getId()))
                .loginId(savedMember.getLoginId())
                .nickname(savedMember.getNickname())
                .name(savedMember.getName())
                .birthDate(savedMember.getBirthDate().toString())
                .email(savedMember.getEmail())
                .phone(savedMember.getPhone())
                .profileUrl(savedMember.getProfileUrl())
                .build();
    }
}
