package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.GeneralLoginRequest;
import com.team1.epilogue.auth.dto.LoginResponse;
import com.team1.epilogue.auth.dto.SocialLoginRequest;
import org.springframework.stereotype.Service;

/**
 * [클래스 레벨]
 * 로그인 관련 비즈니스 로직을 정의하는 서비스 인터페이스 (동기 방식)
 */
@Service
public interface AuthService {
    /**
     * [메서드 레벨]
     * 일반 로그인 기능을 수행
     *
     * @param request 일반 로그인 요청 데이터
     * @return LoginResponse - 로그인 결과 응답
     */
    LoginResponse login(GeneralLoginRequest request);

    /**
     * [메서드 레벨]
     * 소셜 로그인 기능(구글, 카카오 등)을 수행
     *
     * @param request 소셜 로그인 요청 데이터
     * @return LoginResponse - 로그인 결과 응답
     */
    LoginResponse socialLogin(SocialLoginRequest request);
}
