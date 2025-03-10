package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.MemberResponse;
import com.team1.epilogue.auth.dto.UpdateMemberRequest;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.EmailAlreadyExistException;
import com.team1.epilogue.auth.exception.IdAlreadyExistException;
import com.team1.epilogue.auth.exception.InvalidBirthDateFormatException;
import com.team1.epilogue.auth.exception.InvalidEmailFormatException;
import com.team1.epilogue.auth.exception.InvalidPasswordException;
import com.team1.epilogue.auth.exception.InvalidPhoneFormatException;
import com.team1.epilogue.auth.exception.MissingRequiredFieldException;
import com.team1.epilogue.auth.exception.NicknameAlreadyExistsException;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberResponse registerMember(RegisterRequest request) {
        validateRegisterRequest(request);
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new IdAlreadyExistException();
        }
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistException();
        }
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
        Member savedMember = memberRepository.save(member);
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

    private void validateRegisterRequest(RegisterRequest request) {
        if (request.getLoginId() == null || request.getLoginId().isBlank()) {
            throw new MissingRequiredFieldException();
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new InvalidPasswordException();
        }
        if (request.getNickname() == null || request.getNickname().isBlank()) {
            throw new MissingRequiredFieldException();
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new MissingRequiredFieldException();
        }
        if (request.getEmail() == null || request.getEmail().isBlank() ||
                !Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", request.getEmail())) {
            throw new InvalidEmailFormatException();
        }
        if (request.getPhone() == null || request.getPhone().isBlank() ||
                !Pattern.matches("\\d{2,3}-\\d{3,4}-\\d{4}", request.getPhone())) {
            throw new InvalidPhoneFormatException();
        }
        if (request.getBirthDate() == null || request.getBirthDate().isBlank() ||
                !Pattern.matches("\\d{4}-\\d{2}-\\d{2}", request.getBirthDate())) {
            throw new InvalidBirthDateFormatException();
        }
    }

    public MemberResponse updateMember(Long memberId, UpdateMemberRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다."));
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new InvalidEmailFormatException();
        }
        memberRepository.findByEmail(request.getEmail())
                .filter(m -> !m.getId().equals(memberId))
                .ifPresent(m -> { throw new EmailAlreadyExistException(); });
        memberRepository.findByNickname(request.getNickname())
                .filter(m -> !m.getId().equals(memberId))
                .ifPresent(m -> { throw new NicknameAlreadyExistsException(); });
        member.setNickname(request.getNickname());
        member.setEmail(request.getEmail());
        member.setPhone(request.getPhone());
        member.setProfileUrl(request.getProfilePhoto());
        Member updatedMember = memberRepository.save(member);
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

    public Member findOrCreateSocialMember(String email, String loginId, String name, String profileUrl, String socialType) {
        return memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .loginId(loginId)
                                .password("")
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
