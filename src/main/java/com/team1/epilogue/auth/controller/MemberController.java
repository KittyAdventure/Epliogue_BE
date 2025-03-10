package com.team1.epilogue.auth.controller;

import com.team1.epilogue.auth.dto.ApiResponse;
import com.team1.epilogue.auth.dto.MemberResponse;
import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.SuccessResponse;
import com.team1.epilogue.auth.dto.UpdateMemberRequest;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.service.MemberService;
import com.team1.epilogue.auth.service.MemberWithdrawalService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {

  private final MemberService memberService;
  private final MemberWithdrawalService memberWithdrawalService;
  private static final Logger logger = LoggerFactory.getLogger(MemberController.class);


  @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<MemberResponse>> registerMember(
          @RequestBody @Validated RegisterRequest request) {
    MemberResponse memberResponse = memberService.registerMember(request);
    ApiResponse<MemberResponse> response = new ApiResponse<>(true, memberResponse, null, "Registration success");
    return ResponseEntity.ok(response);
  }

  @DeleteMapping
  public ResponseEntity<ApiResponse<SuccessResponse>> withdrawMember(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      ApiResponse<SuccessResponse> errorResponse = new ApiResponse<>(false, null, "Unauthorized user", null);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    Object principal = authentication.getPrincipal();
    logger.info("Principal 클래스: {}", principal.getClass().getName());
    if (principal instanceof CustomMemberDetails) {
      CustomMemberDetails userDetails = (CustomMemberDetails) principal;
      logger.info("Member 정보: {}", userDetails.getMember());
      logger.info("Member loginId: {}", userDetails.getMember().getLoginId());
    } else {
      logger.info("Principal is not an instance of CustomMemberDetails");
    }


    Long memberId = ((CustomMemberDetails) authentication.getPrincipal()).getId();
    memberWithdrawalService.withdrawMember(memberId);
    SuccessResponse success = new SuccessResponse("User account deleted successfully");
    ApiResponse<SuccessResponse> response = new ApiResponse<>(true, success, null, "Deletion success");
    return ResponseEntity.ok(response);
  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<MemberResponse>> updateMember(
          @RequestBody @Validated UpdateMemberRequest request,
          Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      ApiResponse<MemberResponse> errorResponse = new ApiResponse<>(false, null, "Unauthorized", null);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    Long memberId = ((CustomMemberDetails) authentication.getPrincipal()).getId();
    MemberResponse updatedMember = memberService.updateMember(memberId, request);
    if (updatedMember == null) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(new ApiResponse<>(false, null, "Update failed", null));
    }
    ApiResponse<MemberResponse> response = new ApiResponse<>(true, updatedMember, null, "User information updated successfully");
    return ResponseEntity.ok(response);
  }
}
