package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.KakaoUserInfo;
import com.team1.epilogue.auth.exception.KakaoUserInfoException;
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
 * KakaoAuthService는 카카오 OAuth2 로그인에서 사용자 정보를 가져오는 역할을 담당.
 * 인가 코드(authorization code)를 통해 액세스 토큰을 발급받고, 이를 사용하여 사용자 정보를 요청합니다.
 */
@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    /**
     * [필드 레벨]
     * RestClient를 Bean으로 주입받아 재사용 (RestClientConfig에서 관리)
     */
    private final RestClient restClient;

    /**
     * [필드 레벨]
     * 카카오 애플리케이션의 클라이언트 ID
     */
    @Value("${kakao.clientId}")
    private String kakaoClientId;

    /**
     * [필드 레벨]
     * 카카오 로그인 후 인가 코드가 전달될 리디렉션 URI
     */
    @Value("${kakao.redirectUri}")
    private String kakaoRedirectUri;

    // 클라이언트 시크릿이 필요하다면 아래 주석 해제
    // @Value("${kakao.clientSecret}")
    // private String kakaoClientSecret;

    /**
     * [메서드 레벨]
     * 인가 코드를 사용하여 액세스 토큰을 획득하는 메서드.
     *
     * @param authorizationCode 카카오에서 발급받은 인가 코드
     * @return 액세스 토큰 문자열
     * @throws KakaoUserInfoException 액세스 토큰 발급 실패 시 예외 발생
     */
    private String getAccessToken(String authorizationCode) {
        String url = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", authorizationCode);
        // params.add("client_secret", kakaoClientSecret); // 필요 시

        try {
            Map<String, Object> response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(params)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            String accessToken = (String) response.get("access_token");
            if (accessToken == null) {
                throw KakaoUserInfoException.fromEmptyResponse();
            }
            return accessToken;
        } catch (RestClientResponseException e) {
            // HTTP 응답 오류 (4xx, 5xx)
            throw KakaoUserInfoException.fromClientError(e);
        } catch (RestClientException e) {
            // 타임아웃, 네트워크 오류 등
            throw KakaoUserInfoException.fromTimeoutOrNetworkError(e);
        }
    }

    /**
     * [메서드 레벨]
     * 카카오 인가 코드를 사용하여 사용자 정보를 가져오는 메서드.
     *
     * @param authorizationCode 카카오에서 발급받은 인가 코드
     * @return KakaoUserInfo 객체 (성공 시)
     * @throws KakaoUserInfoException 인증 실패 시 예외 발생
     */
    public KakaoUserInfo getKakaoUserInfo(String authorizationCode) {
        // 1. 인가 코드를 사용해 액세스 토큰 획득
        String accessToken = getAccessToken(authorizationCode);

        // 2. 액세스 토큰을 사용하여 사용자 정보 요청
        String url = "https://kapi.kakao.com/v2/user/me";

        try {
            KakaoUserInfo kakaoUserInfo = restClient.get()
                    .uri(url)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(KakaoUserInfo.class);

            if (kakaoUserInfo == null) {
                throw KakaoUserInfoException.fromEmptyResponse();
            }
            return kakaoUserInfo;
        } catch (RestClientResponseException e) {
            // HTTP 응답 오류 (4xx, 5xx)
            throw KakaoUserInfoException.fromClientError(e);
        } catch (RestClientException e) {
            // 타임아웃, 네트워크 오류 등
            throw KakaoUserInfoException.fromTimeoutOrNetworkError(e);
        }
    }
}
