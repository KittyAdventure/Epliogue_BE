package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.KakaoUserInfo;
import com.team1.epilogue.auth.exception.KakaoUserInfoException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * [클래스 레벨]
 * KakaoAuthService는 카카오 OAuth2 로그인에서 사용자 정보를 가져오는 역할을 담당.
 */
@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    /**
     * [필드 레벨]
     * restTemplate: REST API 요청을 수행하는 객체
     */
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * [메서드 레벨]
     * 카카오 OAuth2 인증을 통해 사용자 정보를 가져오는 메서드.
     *
     * @param accessToken 카카오에서 발급받은 액세스 토큰
     * @return KakaoUserInfo 객체 (성공 시)
     * @throws KakaoUserInfoException 인증 실패 시 예외 발생
     */
    public KakaoUserInfo getKakaoUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(url, HttpMethod.GET, request, KakaoUserInfo.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw KakaoUserInfoException.fromStatusCode(response.getStatusCode().value(), response.getBody().toString());
            }
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw KakaoUserInfoException.fromClientError(e);
        } catch (RestClientException e) {
            throw KakaoUserInfoException.fromTimeoutOrNetworkError(e);
        } catch (Exception e) {
            throw KakaoUserInfoException.fromUnknownError(e);
        }
    }
}
