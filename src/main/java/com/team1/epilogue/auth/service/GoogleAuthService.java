package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.GoogleUserInfo;
import com.team1.epilogue.auth.exception.GoogleUserInfoException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.clientId}")
    private String googleClientId;

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
            // 클라이언트 오류 (4xx)
            throw GoogleUserInfoException.fromClientError(e);
        } catch (RestClientException e) {
            // 타임아웃이나 네트워크 오류 등 기타 RestClient 관련 예외 처리
            throw GoogleUserInfoException.fromTimeoutOrNetworkError(e);
        } catch (Exception e) {
            // 그 외 알 수 없는 예외 처리
            throw GoogleUserInfoException.fromUnknownError(e);
        }
    }
}
