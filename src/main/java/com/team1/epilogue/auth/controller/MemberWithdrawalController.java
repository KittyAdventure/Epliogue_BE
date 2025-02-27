package com.team1.epilogue.auth.controller;

import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.service.MemberWithdrawalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * [클래스 레벨]
 * 회원 탈퇴 관련 HTTP 요청을 처리하는 REST 컨트롤러
 * 이 클래스는 "/api/members" 경로로 들어오는 DELETE 요청을 수신하고,
 * 회원 탈퇴와 관련된 비즈니스 로직을 MemberWithdrawalService에 위임
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberWithdrawalController {

    /**
     * [필드 레벨]
     * withdrawalService: 회원 탈퇴 처리를 담당하는 서비스 객체
     * MemberWithdrawalService를 통해 실제 회원 탈퇴 로직을 수행
     */
    private final MemberWithdrawalService withdrawalService;

    /**
     * [메서드 레벨]
     * withdrawMember: 회원 탈퇴 요청을 처리하는 메서드
     * 인증된 사용자만 탈퇴할 수 있도록 처리하며, 인증되지 않은 사용자가 요청할 경우
     * 에러 메시지를 반환
     *
     * @param authentication 인증된 사용자 정보
     * @return ResponseEntity 탈퇴 성공 또는 실패 메시지
     */
    @DeleteMapping
    public ResponseEntity<?> withdrawMember(Authentication authentication) {
        // 인증된 사용자가 아니면 에러 응답을 반환
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400); // 에러 상태 코드를 400으로 설정
            errorResponse.put("errorCode", "UNAUTHORIZED"); // 에러 코드 설정
            errorResponse.put("message", "인증되지 않은 사용자"); // 에러 메시지 설정
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // 인증된 사용자의 ID를 가져옴
        Long memberId = ((CustomMemberDetails) authentication.getPrincipal()).getId();

        // 회원 탈퇴를 수행
        withdrawalService.withdrawMember(memberId);

        // 탈퇴 성공 메시지를 반환
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "유저의 계정이 정상적으로 삭제");
        return ResponseEntity.ok(successResponse); // 성공 시 200 OK 응답 반환
    }
}
