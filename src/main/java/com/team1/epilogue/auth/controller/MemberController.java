package com.team1.epilogue.auth.controller;

import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.MemberResponse;
import com.team1.epilogue.auth.dto.UpdateMemberRequest;
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
 * 회원 관리 컨트롤러 (회원 가입, 정보 수정, 탈퇴)
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;
  private final MemberWithdrawalService memberWithdrawalService;

  /**
   * [메서드 레벨]
   * 회원 가입 API
   *
   * @param request 회원 가입 요청 데이터
   * @return 생성된 회원 정보 반환
   */
  @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
  public MemberResponse registerMember(@RequestBody @Validated RegisterRequest request) {
    return memberService.registerMember(request);
  }

  /**
   * [메서드 레벨]
   * 회원 탈퇴 API
   *
   * @param authentication 현재 인증된 사용자 정보
   * @return 성공 메시지 반환
   */
  @DeleteMapping
  public ResponseEntity<?> withdrawMember(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return createErrorResponse(401, "UNAUTHORIZED", "인증되지 않은 사용자");
    }
    Long memberId = ((CustomMemberDetails) authentication.getPrincipal()).getId();
    memberWithdrawalService.withdrawMember(memberId);

    return createSuccessResponse("유저의 계정이 정상적으로 삭제되었습니다.");
  }

  /**
   * [메서드 레벨]
   * 회원 정보 수정 API
   *
   * @param request        회원 정보 수정 요청 데이터
   * @param authentication 현재 인증된 사용자 정보
   * @return 수정된 회원 정보 반환
   */
  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateMember(@RequestBody @Validated UpdateMemberRequest request,
                                        Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return createErrorResponse(401, "UNAUTHORIZED", "인증 실패");
    }
    Long memberId = ((CustomMemberDetails) authentication.getPrincipal()).getId();
    MemberResponse updatedMember = memberService.updateMember(memberId, request);

    Map<String, Object> response = new HashMap<>();
    response.put("메시지", "유저 정보 수정 완료");
    response.put("유저", updatedMember);
    return ResponseEntity.ok(response);
  }

  /**
   * [메서드 레벨]
   * 에러 응답 생성 메서드
   *
   * @param status    HTTP 상태 코드
   * @param errorCode 오류 코드
   * @param message   오류 메시지
   * @return 에러 응답 ResponseEntity
   */
  private ResponseEntity<Map<String, Object>> createErrorResponse(int status, String errorCode, String message) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("상태", status);
    errorResponse.put("에러코드", errorCode);
    errorResponse.put("메시지", message);
    return ResponseEntity.status(HttpStatus.valueOf(status)).body(errorResponse);
  }

  /**
   * [메서드 레벨]
   * 성공 응답 생성 메서드
   *
   * @param message 성공 메시지
   * @return 성공 응답 ResponseEntity
   */
  private ResponseEntity<Map<String, String>> createSuccessResponse(String message) {
    Map<String, String> successResponse = new HashMap<>();
    successResponse.put("메시지", message);
    return ResponseEntity.ok(successResponse);
  }
}
