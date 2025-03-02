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

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 전달받은 액세스 토큰을 사용하여 카카오 API로부터 사용자 정보를 가져옵니다.
     * 발생 가능한 예외 상황은 KakaoUserInfoException의 정적 팩토리 메서드를 통해 처리합니다.
     *
     * @param accessToken 카카오에서 발급받은 액세스 토큰
     * @return KakaoUserInfo 객체 (정상 응답 시)
     * @throws KakaoUserInfoException 오류 발생 시 해당 예외를 던짐
     */
    public KakaoUserInfo getKakaoUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(url, HttpMethod.GET, request, KakaoUserInfo.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                String responseBody = (response.getBody() != null) ? response.getBody().toString() : "";
                throw KakaoUserInfoException.fromStatusCode(response.getStatusCode().value(), responseBody);
            }
            return response.getBody();
        } catch (HttpClientErrorException e) {
            // 클라이언트 오류 발생 시 처리 (4xx)
            throw KakaoUserInfoException.fromClientError(e);
        } catch (RestClientException e) {
            // 타임아웃, 네트워크 오류 등 기타 RestClient 관련 예외 처리
            throw KakaoUserInfoException.fromTimeoutOrNetworkError(e);
        } catch (Exception e) {
            // 그 외 알 수 없는 예외 처리
            throw KakaoUserInfoException.fromUnknownError(e);
        }
    }
}
