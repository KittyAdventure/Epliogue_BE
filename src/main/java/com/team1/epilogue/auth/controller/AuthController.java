package com.team1.epilogue.auth.controller;

import com.team1.epilogue.auth.dto.*;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * [클래스 레벨]
 * 인증 및 로그인 관련 HTTP 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final KakaoAuthService kakaoAuthService;
    private final GoogleAuthService googleAuthService;
    private final MemberWithdrawalService memberWithdrawalService;
    private final GoogleWithdrawalService googleWithdrawalService;
    private final KakaoWithdrawalService kakaoWithdrawalService;
    private final LogoutService logoutService;
    //private final LogoutService logoutService;

    /**
     * [메서드 레벨]
     * 일반 로그인 API
     *
     * @param request 일반 로그인 요청 데이터 (ID, 비밀번호 포함)
     * @return LoginResponse - JWT 토큰 및 사용자 정보 반환
     * @throws BadCredentialsException 아이디 또는 비밀번호가 올바르지 않을 경우 예외 발생
     */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody GeneralLoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

//    /**
//     * [메서드 레벨]
//     * 소셜 로그인 API (Google, Kakao 등)
//     *
//     * @param request 소셜 로그인 요청 데이터 (provider, accessToken 포함)
//     * @return LoginResponse - JWT 토큰 및 사용자 정보 반환
//     * @throws IllegalArgumentException 지원되지 않는 소셜 로그인 제공자가 입력된 경우 예외 발생
//     */
//    @PostMapping(value = "/login/social", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> socialLogin(@RequestBody SocialLoginRequest request) {
//        try {
//            LoginResponse response = authService.socialLogin(request);
//            return ResponseEntity.ok(response);
//        } catch (IllegalArgumentException ex) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", ex.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
//        }
//    }

    /**
     * [메서드 레벨]
     * 카카오 로그인 콜백 엔드포인트
     * 카카오 인증 코드(code)를 받아 사용자 정보를 조회하고, 로그인 처리 후 JWT 토큰을 반환
     *
     * @param code 카카오에서 제공한 인증 코드
     * @return 로그인 응답 (JWT 토큰 및 사용자 정보)
     */
    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {
        try {
            KakaoUserInfo kakaoUserInfo = kakaoAuthService.getKakaoUserInfo(code);
            LoginResponse response = authService.socialLoginKakao(kakaoUserInfo);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    /**
     * [메서드 레벨]
     * 구글 로그인 콜백 엔드포인트
     * 구글 인증 코드(code)를 받아 사용자 정보를 조회하고, 로그인 처리 후 JWT 토큰을 반환
     *
     * @param code 구글에서 제공한 인증 코드
     * @return 로그인 응답 (JWT 토큰 및 사용자 정보)
     */
    @GetMapping("/auth/google/callback")
    public ResponseEntity<?> googleCallback(@RequestParam("code") String code) {
        try {
            GoogleUserInfo googleUserInfo = googleAuthService.getGoogleUserInfo(code);
            LoginResponse response = authService.socialLoginGoogle(googleUserInfo);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    /**
     * [메서드 레벨]
     * 일반 로그아웃 엔드포인트
     * Authorization 헤더에서 JWT 토큰을 추출하여 무효화 처리
     *
     * @param request HTTP 요청 객체
     * @return 로그아웃 성공 메시지
     */
    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Authorization header missing or invalid"));
        }
        String token = authHeader.substring(7);
        logoutService.invalidate(token);
        return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
    }

    /**
     * [메서드 레벨]
     * 소셜 로그아웃 엔드포인트
     * Authorization 헤더에서 JWT 토큰을 추출하여 무효화 처리
     *
     * @param request HTTP 요청 객체
     * @return 소셜 로그아웃 성공 메시지
     */
    @PostMapping(value = "/logout/social", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> socialLogout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Authorization header missing or invalid"));
        }
        String token = authHeader.substring(7);
        logoutService.invalidate(token);
        return ResponseEntity.ok(Map.of("message", "소셜 로그아웃 성공"));
    }

    /**
     * [메서드 레벨]
     * 소셜 회원 탈퇴 엔드포인트
     * 카카오/구글 연동 해제 후, 해당 사용자의 계정을 삭제
     *
     * @param provider    소셜 로그인 제공자 (kakao, google)
     * @param accessToken 사용자의 소셜 액세스 토큰
     * @param authentication 현재 로그인된 사용자 정보
     * @return 소셜 회원 탈퇴 결과 메시지
     */
    @DeleteMapping(value = "/social/withdraw", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> withdrawSocialMember(@RequestParam("provider") String provider,
                                                  @RequestParam("accessToken") String accessToken,
                                                  Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() ||
            !(authentication.getPrincipal() instanceof CustomMemberDetails)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 401);
            errorResponse.put("error", "UNAUTHORIZED");
            errorResponse.put("message", "인증되지 않은 사용자");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        try {
            if ("kakao".equalsIgnoreCase(provider)) {
                // 카카오 서버와 연동 해제 호출
                kakaoWithdrawalService.unlinkKakaoAccount(accessToken);
            } else if ("google".equalsIgnoreCase(provider)) {
                googleWithdrawalService.revokeGoogleAccount(accessToken);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "지원되지 않는 소셜 제공자입니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            // DB 회원 삭제: 인증 정보를 통해 현재 회원 ID를 가져와서 탈퇴 처리
            Member member = (Member) authentication.getPrincipal();
            memberWithdrawalService.withdrawMember(member.getId());
            Map<String, String> success = new HashMap<>();
            success.put("message", "소셜 회원 탈퇴 성공");
            return ResponseEntity.ok(success);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}


