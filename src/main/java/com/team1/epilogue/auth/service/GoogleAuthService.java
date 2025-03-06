package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.GoogleUserInfo;
import com.team1.epilogue.auth.exception.GoogleUserInfoException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

/**
 * [클래스 레벨]
 * GoogleAuthService는 구글 OAuth2 로그인에서 사용자 정보를 가져오는 역할을 담당.
 * 인가 코드를 사용하여 액세스 토큰을 발급받고, 이를 사용하여 사용자 정보를 요청합니다.
 */
@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    /**
     * [필드 레벨]
     * RestClient를 Bean으로 주입받아 재사용 (RestClientConfig에서 관리)
     */
    private final RestClient restClient;

    /**
     * [필드 레벨]
     * googleClientId: 애플리케이션의 구글 클라이언트 ID (application.properties에서 주입)
     */
    @Value("${google.clientId}")
    private String googleClientId;

    /**
     * [필드 레벨]
     * googleClientSecret: 애플리케이션의 구글 클라이언트 시크릿 (application.properties에서 주입)
     */
    @Value("${google.clientSecret}")
    private String googleClientSecret;

    /**
     * [필드 레벨]
     * googleRedirectUri: 구글 로그인 후 인가 코드가 전달될 리디렉션 URI (application.properties에서 주입)
     */
    @Value("${google.redirectUri}")
    private String googleRedirectUri;


    /**
     * [메서드 레벨]
     * 인가 코드를 사용하여 액세스 토큰을 발급받는 메서드.
     *
     * @param authorizationCode 구글에서 발급받은 인가 코드
     * @return 액세스 토큰 문자열
     * @throws GoogleUserInfoException 액세스 토큰 발급 실패 시 예외 발생
     */
    private String getAccessToken(String authorizationCode) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        try {
            Map<String, Object> response = restClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(params)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            String accessToken = (String) response.get("access_token");
            if (accessToken == null) {
                throw GoogleUserInfoException.fromEmptyResponse();
            }
            return accessToken;
        } catch (RestClientResponseException e) {
            // HTTP 응답 오류 (4xx, 5xx)
            throw GoogleUserInfoException.fromClientError(e);
        } catch (RestClientException e) {
            // 타임아웃, 네트워크 오류 등
            throw GoogleUserInfoException.fromTimeoutOrNetworkError(e);
        }
    }

    /**
     * [메서드 레벨]
     * 구글 OAuth2 인가 코드를 사용하여 사용자 정보를 가져오는 메서드.
     *
     * @param authorizationCode 구글에서 발급받은 인가 코드
     * @return GoogleUserInfo 객체 (성공 시)
     * @throws GoogleUserInfoException 인증 실패 시 예외 발생
     */
    public GoogleUserInfo getGoogleUserInfo(String authorizationCode) {
        // 1. 인가 코드를 사용해 액세스 토큰 획득
        String accessToken = getAccessToken(authorizationCode);

        // 2. 획득한 액세스 토큰을 사용하여 사용자 정보 요청
        String url = "https://oauth2.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;

        try {
            GoogleUserInfo googleUserInfo = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(GoogleUserInfo.class);

            if (googleUserInfo == null) {
                throw GoogleUserInfoException.fromEmptyResponse();
            }
            if (!googleClientId.equals(googleUserInfo.getAud())) {
                throw GoogleUserInfoException.fromClientIdMismatch();
            }
            return googleUserInfo;
        } catch (RestClientResponseException e) {
            // HTTP 응답 오류 (4xx, 5xx)
            throw GoogleUserInfoException.fromClientError(e);
        } catch (RestClientException e) {
            // 타임아웃, 네트워크 오류 등
            throw GoogleUserInfoException.fromTimeoutOrNetworkError(e);
        }
    }
}
