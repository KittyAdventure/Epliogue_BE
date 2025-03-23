package com.team1.epilogue.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleWithdrawalService {

    private final RestClient restClient;

    @Value("${google.clientId}")
    private String googleClientId;

    /**
     * Google 계정 연결 해제 API 호출.
     * Google에서는 토큰을 revoke하는 URL을 사용합니다.
     *
     * @param accessToken Google 액세스 토큰
     */
    public void revokeGoogleAccount(String accessToken) {
        String url = "https://oauth2.googleapis.com/revoke?token=" + accessToken;
        try {
            // POST 방식으로 호출 (GET 방식이 아닌)
            restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {
                    });
        } catch (RestClientResponseException e) {
            throw new RuntimeException("Google revoke 실패: " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            throw new RuntimeException("Google revoke 에러: " + e.getMessage(), e);
        }
    }
}