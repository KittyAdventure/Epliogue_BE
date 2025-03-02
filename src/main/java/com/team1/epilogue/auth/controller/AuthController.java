package com.team1.epilogue.auth.controller;

import com.team1.epilogue.auth.dto.GeneralLoginRequest;
import com.team1.epilogue.auth.dto.LoginResponse;
import com.team1.epilogue.auth.dto.SocialLoginRequest;
import com.team1.epilogue.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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
            error.put("에러", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * [메서드 레벨]
     * 소셜 로그인 API (Google, Kakao 등)
     *
     * @param request 소셜 로그인 요청 데이터 (provider, accessToken 포함)
     * @return LoginResponse - JWT 토큰 및 사용자 정보 반환
     * @throws IllegalArgumentException 지원되지 않는 소셜 로그인 제공자가 입력된 경우 예외 발생
     */
    @PostMapping(value = "/login/social", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> socialLogin(@RequestBody SocialLoginRequest request) {
        try {
            LoginResponse response = authService.socialLogin(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("에러", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
