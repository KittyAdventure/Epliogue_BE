package com.team1.epilogue.auth.controller;

import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.MemberResponse;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.service.MemberService;
import com.team1.epilogue.auth.service.MemberWithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * [클래스 레벨]
 * 사용자 관련 HTTP 요청을 처리하는 REST 컨트롤러
 * "/api/members" 경로로 들어오는 요청을 수신하며, 사용자 등록과 회원 탈퇴 기능을 담당
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    /**
     * [필드 레벨]
     * 사용자 등록 및 기타 사용자 관련 기능을 수행하는 서비스 클래스
     */
    private final MemberService memberService;

    /**
     * [필드 레벨]
     * 회원 탈퇴 처리를 담당하는 서비스 클래스
     */
    private final MemberWithdrawalService memberWithdrawalService;

    /**
     * [메서드 레벨]
     * registerMember API 엔드포인트는 클라이언트가 JSON 형식의 사용자 등록 요청을 전송하면,
     * 이를 검증(@Validated)하고, MemberService를 호출하여 새로운 사용자를 등록한 후,
     * 결과로 MemberResponse 객체를 반환
     *
     * @param request 클라이언트가 전송한 사용자 등록 요청 데이터
     * @return 등록된 사용자의 정보를 담은 MemberResponse 객체
     */
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public MemberResponse registerMember(@Validated @RequestBody RegisterRequest request) {
        return memberService.registerMember(request);
    }

    /**
     * [메서드 레벨]
     * withdrawMember는 회원 탈퇴 요청을 처리하는 메서드
     * 인증된 사용자만 탈퇴할 수 있도록 처리하며, 인증되지 않은 사용자가 요청할 경우
     * 에러 메시지를 반환
     *
     * @param authentication 인증된 사용자 정보
     * @return ResponseEntity 회원 탈퇴 성공 또는 실패 메시지
     */
    @DeleteMapping
    public ResponseEntity<?> withdrawMember(Authentication authentication) {
        // 인증된 사용자가 아니면 에러 응답을 반환
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);         // 에러 상태 코드를 400으로 설정
            errorResponse.put("errorCode", "UNAUTHORIZED");  // 에러 코드 설정
            errorResponse.put("message", "인증되지 않은 사용자"); // 에러 메시지 설정
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // 인증된 사용자의 ID를 가져
        Long memberId = ((CustomMemberDetails) authentication.getPrincipal()).getId();

        // 회원 탈퇴를 수행
        memberWithdrawalService.withdrawMember(memberId);

        // 탈퇴 성공 메시지를 반환
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "유저의 계정이 정상적으로 삭제");
        return ResponseEntity.ok(successResponse);  // 성공 시 200 OK 응답 반환
    }
}
