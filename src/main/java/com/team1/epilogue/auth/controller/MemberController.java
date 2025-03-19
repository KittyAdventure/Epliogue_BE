package com.team1.epilogue.auth.controller;

import com.team1.epilogue.auth.dto.ApiResponse;
import com.team1.epilogue.auth.dto.MemberResponse;
import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.SearchMemberResponseDto;
import com.team1.epilogue.auth.dto.SuccessResponse;
import com.team1.epilogue.auth.dto.UpdateMemberRequest;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.service.MemberService;
import com.team1.epilogue.auth.service.MemberWithdrawalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {

  private final MemberService memberService;
  private final MemberWithdrawalService memberWithdrawalService;

  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<MemberResponse>> registerMember(
          @RequestPart("data") @Validated RegisterRequest request,
          @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
    MemberResponse memberResponse = memberService.registerMember(request, profileImage);
    ApiResponse<MemberResponse> response = new ApiResponse<>(true, memberResponse, null, "Registration success");
    return ResponseEntity.ok(response);
  }

  @DeleteMapping
  public ResponseEntity<ApiResponse<SuccessResponse>> withdrawMember(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      ApiResponse<SuccessResponse> errorResponse = new ApiResponse<>(false, null, "Unauthorized user", null);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    Long memberId = ((CustomMemberDetails) authentication.getPrincipal()).getId();
    memberWithdrawalService.withdrawMember(memberId);
    SuccessResponse success = new SuccessResponse("User account deleted successfully");
    ApiResponse<SuccessResponse> response = new ApiResponse<>(true, success, null, "Deletion success");
    return ResponseEntity.ok(response);
  }

  @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<MemberResponse>> updateMember(
          @RequestPart("data") @Validated UpdateMemberRequest request,
          @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
          Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      ApiResponse<MemberResponse> errorResponse = new ApiResponse<>(false, null, "Unauthorized", null);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    Long memberId = ((CustomMemberDetails) authentication.getPrincipal()).getId();
    MemberResponse updatedMember = memberService.updateMember(memberId, request, profileImage);
    if (updatedMember == null) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(new ApiResponse<>(false, null, "Update failed", null));
    }
    ApiResponse<MemberResponse> response = new ApiResponse<>(true, updatedMember, null, "User information updated successfully");
    return ResponseEntity.ok(response);
  }

  // 회원 아이디 검색
  @GetMapping("/search")
  public ResponseEntity<ApiResponse<Page<SearchMemberResponseDto>>> searchMember(@RequestParam String searchType,
    @RequestParam String keyword,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "9") int size,
    @RequestParam(required = false) Boolean hasProfileUrl,
    @RequestParam(required = false, defaultValue = "latest") String sortType
  )
  {
    Pageable pageable = PageRequest.of(page, size);  // 한 페이지당 9개씩
    Page<SearchMemberResponseDto> members = memberService.searchMember(searchType,keyword,pageable,hasProfileUrl,sortType);
    ApiResponse<Page<SearchMemberResponseDto>> response = new ApiResponse<>(true,members,null,"Search success");
    return ResponseEntity.ok(response);
  }
}
