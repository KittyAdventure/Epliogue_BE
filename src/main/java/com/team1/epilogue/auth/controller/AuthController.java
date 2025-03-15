package com.team1.epilogue.auth.controller;

import com.team1.epilogue.auth.dto.ApiResponse;
import com.team1.epilogue.auth.dto.ErrorResponse;
import com.team1.epilogue.auth.dto.GeneralLoginRequest;
import com.team1.epilogue.auth.dto.GoogleUserInfo;
import com.team1.epilogue.auth.dto.KakaoUserInfo;
import com.team1.epilogue.auth.dto.LoginResponse;
import com.team1.epilogue.auth.dto.SuccessResponse;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.service.AuthService;
import com.team1.epilogue.auth.service.GoogleAuthService;
import com.team1.epilogue.auth.service.KakaoAuthService;
import com.team1.epilogue.auth.service.GoogleWithdrawalService;
import com.team1.epilogue.auth.service.KakaoWithdrawalService;
import com.team1.epilogue.auth.service.LogoutService;
import com.team1.epilogue.auth.service.MemberWithdrawalService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class AuthController {

    private final AuthService authService;
    private final KakaoAuthService kakaoAuthService;
    private final GoogleAuthService googleAuthService;
    private final MemberWithdrawalService memberWithdrawalService;
    private final GoogleWithdrawalService googleWithdrawalService;
    private final KakaoWithdrawalService kakaoWithdrawalService;
    private final LogoutService logoutService;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody GeneralLoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            ApiResponse<LoginResponse> apiResponse = new ApiResponse<>(true, response, null, "Login success");
            return ResponseEntity.ok(apiResponse);
        } catch (BadCredentialsException ex) {
            ApiResponse<LoginResponse> errorResponse = new ApiResponse<>(false, null, ex.getMessage(), "Login failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<ApiResponse<LoginResponse>> kakaoCallback(@RequestParam("code") String code) {
        try {
            KakaoUserInfo kakaoUserInfo = kakaoAuthService.getKakaoUserInfo(code);
            LoginResponse response = authService.socialLoginKakao(kakaoUserInfo);
            ApiResponse<LoginResponse> apiResponse = new ApiResponse<>(true, response, null, "Login success");
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            ApiResponse<LoginResponse> errorResponse = new ApiResponse<>(false, null, ex.getMessage(), "Kakao login failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/auth/google/callback")
    public ResponseEntity<ApiResponse<LoginResponse>> googleCallback(@RequestParam("code") String code) {
        try {
            GoogleUserInfo googleUserInfo = googleAuthService.getGoogleUserInfo(code);
            LoginResponse response = authService.socialLoginGoogle(googleUserInfo);
            ApiResponse<LoginResponse> apiResponse = new ApiResponse<>(true, response, null, "Login success");
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            ApiResponse<LoginResponse> errorResponse = new ApiResponse<>(false, null, ex.getMessage(), "Google login failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SuccessResponse>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ApiResponse<SuccessResponse> errorResponse = new ApiResponse<>(false, null, "Authorization header missing or invalid", "Logout failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        String token = authHeader.substring(7);
        logoutService.invalidate(token);
        SuccessResponse success = new SuccessResponse("Logout Success");
        ApiResponse<SuccessResponse> apiResponse = new ApiResponse<>(true, success, null, "Logout successful");
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping(value = "/logout/social", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SuccessResponse>> socialLogout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ApiResponse<SuccessResponse> errorResponse = new ApiResponse<>(false, null, "Authorization header missing or invalid", "Social logout failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        String token = authHeader.substring(7);
        logoutService.invalidate(token);
        SuccessResponse success = new SuccessResponse("Social Logout Success");
        ApiResponse<SuccessResponse> apiResponse = new ApiResponse<>(true, success, null, "Social logout successful");
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping(value = "/social/withdraw", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SuccessResponse>> withdrawSocialMember(
            @RequestParam("provider") String provider,
            @RequestParam("accessToken") String accessToken,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof CustomMemberDetails)) {
            ApiResponse<SuccessResponse> errorResponse = new ApiResponse<>(false, null, "Unauthorized user", "Withdrawal failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        try {
            if ("kakao".equalsIgnoreCase(provider)) {
                kakaoWithdrawalService.unlinkKakaoAccount(accessToken);
            } else if ("google".equalsIgnoreCase(provider)) {
                googleWithdrawalService.revokeGoogleAccount(accessToken);
            } else {
                ApiResponse<SuccessResponse> errorResponse = new ApiResponse<>(false, null, "Unsupported social provider", "Withdrawal failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            CustomMemberDetails userDetails = (CustomMemberDetails) authentication.getPrincipal();
            Long memberId = userDetails.getId();
            memberWithdrawalService.withdrawMember(memberId);

            SuccessResponse success = new SuccessResponse("Social member withdrawal success");
            ApiResponse<SuccessResponse> apiResponse = new ApiResponse<>(true, success, null, "Withdrawal successful");
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            ApiResponse<SuccessResponse> errorResponse = new ApiResponse<>(false, null, ex.getMessage(), "Withdrawal failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
