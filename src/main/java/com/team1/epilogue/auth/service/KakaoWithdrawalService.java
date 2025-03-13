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
public class KakaoWithdrawalService {

    private final RestClient restClient;

    @Value("${kakao.clientId}")
    private String kakaoClientId;

    @Value("${kakao.clientSecret}")
    private String kakaoClientSecret;

    /**
     *
     * 카카오 서버의 연결 해제 API를 호출하여 사용자의 카카오 연결을 해제
     *
     * @param accessToken 카카오 액세스 토큰
     */
    public void unlinkKakaoAccount(String accessToken) {
        String url = "https://kapi.kakao.com/v1/user/unlink";
        try {
            // POST 방식으로 카카오 연결 해제 API 호출
            restClient.post()
                    .uri(url)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
            // 정상 호출시 연결 해제 완료
        } catch (RestClientResponseException e) {
            throw new RuntimeException("카카오 unlink 실패: " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            throw new RuntimeException("카카오 unlink 에러: " + e.getMessage(), e);
        }
    }
}
