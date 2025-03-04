package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.MemberResponse;
import com.team1.epilogue.auth.dto.UpdateMemberRequest;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.IdAlreadyExistException;
import com.team1.epilogue.auth.exception.EmailAlreadyExistException;
import com.team1.epilogue.auth.exception.EmailNotValidException;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * [메서드 레벨]
     * 회원가입 요청(RegisterRequest)을 처리하여 회원 정보를 저장하고,
     * 저장된 정보를 MemberResponse DTO로 반환하는 메서드.
     */
    public MemberResponse registerMember(RegisterRequest request) {
        // 이미 사용 중인 로그인 ID 체크
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new IdAlreadyExistException();
        }
        // 이미 사용 중인 이메일 체크
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistException();
        }
        // 이메일 형식 검증
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new EmailNotValidException();
        }
        // 닉네임 중복 체크 추가
        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }
        // RegisterRequest를 Member 엔티티로 변환
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
                .build();

        // 회원 정보 저장
        Member savedMember = memberRepository.save(member);

        // 저장된 정보를 MemberResponse로 변환하여 반환
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

    /**
     * [메서드 레벨]
     * 회원정보 수정 요청을 처리하여 회원의 닉네임, 이메일, 전화번호, 프로필 사진 정보를 업데이트하고,
     * 업데이트된 정보를 MemberResponse로 반환하는 메서드.
     *
     * @param memberId 수정할 회원의 ID
     * @param request  업데이트할 회원정보가 담긴 DTO
     * @return 업데이트된 회원 정보를 담은 MemberResponse
     */
    public MemberResponse updateMember(Long memberId, UpdateMemberRequest request) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다."));

        // 이메일 형식 검증
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new EmailNotValidException();
        }
        // 이메일 중복 체크: 현재 회원이 아닌 다른 회원이 해당 이메일을 사용 중이면 오류 발생
        memberRepository.findByEmail(request.getEmail())
                .filter(m -> !m.getId().equals(memberId))
                .ifPresent(m -> { throw new EmailAlreadyExistException(); });

        // 닉네임 중복 체크: (원하는 경우)
        memberRepository.findByNickname(request.getNickname())
                .filter(m -> !m.getId().equals(memberId))
                .ifPresent(m -> { throw new RuntimeException("닉네임이 이미 사용 중입니다."); });

    // 회원 정보 업데이트
        member.setNickname(request.getNickname());
        member.setEmail(request.getEmail());
        member.setPhone(request.getPhone());
        member.setProfileUrl(request.getProfilePhoto());

        // 업데이트된 회원 정보 저장
        Member updatedMember = memberRepository.save(member);

        // 업데이트된 정보를 MemberResponse DTO로 변환하여 반환
        return MemberResponse.builder()
                .id(String.valueOf(updatedMember.getId()))
                .loginId(updatedMember.getLoginId())
                .nickname(updatedMember.getNickname())
                .name(updatedMember.getName())
                .birthDate(updatedMember.getBirthDate().toString())
                .email(updatedMember.getEmail())
                .phone(updatedMember.getPhone())
                .profileUrl(updatedMember.getProfileUrl())
                .build();
    }

    /**
     * [메서드 레벨]
     *
     * 소셜 회원가입:
     * - 이메일을 기준으로 이미 등록되어 있다면 해당 회원을 반환
     * - 등록되어 있지 않으면 신규 회원으로 등록
     */
    public Member findOrCreateSocialMember(String email, String loginId, String name, String profileUrl, String socialType) {
        return memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .loginId(loginId)
                                .password("")  // 소셜 회원은 비밀번호 없이 가입
                                .nickname(name)
                                .name(name)
                                .email(email)
                                .phone("")
                                .profileUrl(profileUrl)
                                .point(0)
                                .social(socialType)
                                .build()
                ));
    }
}