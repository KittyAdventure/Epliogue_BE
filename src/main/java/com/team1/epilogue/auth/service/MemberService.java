package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.MemberResponse;
import com.team1.epilogue.auth.dto.SearchMemberResponseDto;
import com.team1.epilogue.auth.dto.UpdateMemberRequest;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.*;
import com.team1.epilogue.auth.repository.CustomMemberRepository;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.service.S3Service;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3StorageService;
    private final CustomMemberRepository customMemberRepository;

    public MemberResponse registerMember(RegisterRequest request, MultipartFile profileImage) {
        validateRegisterRequest(request);
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new IdAlreadyExistException();
        }
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistException();
        }

        String profileUrl = request.getProfileUrl(); // 기본 이미지 URL (프론트엔드에서 기본값 지정 가능)
        if (profileImage != null && !profileImage.isEmpty()) {
            profileUrl = s3StorageService.uploadFile(profileImage);
        }

        Member member = Member.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .name(request.getName())
                .birthDate(LocalDate.parse(request.getBirthDate()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .profileUrl(profileUrl)
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

    public MemberResponse updateMember(Long memberId, UpdateMemberRequest request, MultipartFile profileImage) {
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

        if (profileImage != null && !profileImage.isEmpty()) {
            if (member.getProfileUrl() != null && !member.getProfileUrl().isBlank()) {
                s3StorageService.deleteFile(member.getProfileUrl());
            }
            String newProfileUrl = s3StorageService.uploadFile(profileImage);
            member.setProfileUrl(newProfileUrl);
        }

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

    public Page<SearchMemberResponseDto> searchMember(String searchType, String keyword, Pageable pageable, Boolean hasProfileUrl, String sortType) {

        Page<Member> members = customMemberRepository.searchMembers(searchType,keyword,pageable,hasProfileUrl,sortType);

        return members.map(SearchMemberResponseDto::fromEntity);
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
