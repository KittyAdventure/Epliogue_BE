package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.*;
import org.springframework.stereotype.Service;


@Service
public interface AuthService {

    LoginResponse login(GeneralLoginRequest request);
    LoginResponse socialLogin(SocialLoginRequest request);
    LoginResponse socialLoginKakao(KakaoUserInfo kakaoUserInfo);
    LoginResponse socialLoginGoogle(GoogleUserInfo googleUserInfo);
}
