package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.GoogleUserInfo;
import com.team1.epilogue.auth.exception.GoogleUserInfoException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * [클래스 레벨]
 * GoogleAuthService는 구글 OAuth2 로그인에서 사용자 정보를 가져오는 역할을 담당.
 */
@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    /**
     * [필드 레벨]
     * restTemplate: REST API 요청을 수행하는 객체
     */
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * [필드 레벨]
     * googleClientId: 애플리케이션의 구글 클라이언트 ID (application.properties에서 주입)
     */
    @Value("${google.clientId}")
    private String googleClientId;

    /**
     * [메서드 레벨]
     * 구글 OAuth2 인증을 통해 사용자 정보를 가져오는 메서드.
     *
     * @param idToken 사용자의 구글 ID 토큰
     * @return GoogleUserInfo 객체 (성공 시)
     * @throws GoogleUserInfoException 인증 실패 시 예외 발생
     */
    public GoogleUserInfo getGoogleUserInfo(String idToken) {
        String url = "https://oauth2.googleapis.com/oauth2/v2/userinfo?access_token=" + idToken;
        try {
            GoogleUserInfo googleUserInfo = restTemplate.getForObject(url, GoogleUserInfo.class);
            if (googleUserInfo == null) {
                throw GoogleUserInfoException.fromEmptyResponse();
            }
            if (!googleClientId.equals(googleUserInfo.getAud())) {
                throw GoogleUserInfoException.fromClientIdMismatch();
            }
            return googleUserInfo;
        } catch (HttpClientErrorException e) {
            throw GoogleUserInfoException.fromClientError(e);
        } catch (RestClientException e) {
            throw GoogleUserInfoException.fromTimeoutOrNetworkError(e);
        } catch (Exception e) {
            throw GoogleUserInfoException.fromUnknownError(e);
        }
    }
}
