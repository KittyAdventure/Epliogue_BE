package com.team1.epilogue.transaction.client;

import com.team1.epilogue.transaction.dto.KakaoPayApproveRequest;
import com.team1.epilogue.transaction.dto.KakaoPayApproveResponse;
import com.team1.epilogue.transaction.dto.KakaoPayRequest;
import com.team1.epilogue.transaction.dto.KakaoPayResponse;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class KakaoPayClient {

  private final StringRedisTemplate redisTemplate;
  private WebClient webClient;
  private final String KAKAOPAY_BASE_URL = "https://open-api.kakaopay.com";
  @Value("${kakao.pay.cid}")
  private String kakaoPayCid; // 카카오페이 결제를 위한 cid
  @Value("${kakao.pay.apikey}")
  private String kakaoPayApiKey; // 카카오페이 결제를 위한 api key
  private final String KAKAOPAY_PATH = "/online/v1/payment"; // 카카오페이 path
  private final String KAKAOPAY_PREPARE_PATH = "/ready"; // 카카오페이 준비를 위한 path
  private final String KAKAOPAY_APPROVE_PATH = "/approve";
  private final String APPROVAL_URL = "http://localhost:8080/api/kp/success"; // 카카오페이 성공시 redirect 할 URl
  private final String CANCEL_URL = "http://localhost:8080/api/kp/cancel"; // 카카오페이 취소시 redirect 할 URl
  private final String FAIL_URL = "http://localhost:8080/api/kp/fail"; // 카카오페이 실패시 redirect 할 URl

  public KakaoPayClient(StringRedisTemplate redis) {
    this.redisTemplate = redis; // 생성자 방식으로 DI
  }

  @PostConstruct
  private void initWebClient() {
    this.webClient = WebClient.builder()
        .baseUrl(KAKAOPAY_BASE_URL) // base Url 설정
        .defaultHeader(HttpHeaders.AUTHORIZATION, "SECRET_KEY " + kakaoPayApiKey) // api key 설정
        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        .build();
  }

  /**
   * 카카오페이 결제 준비 API 를 호출하는 기능입니다.
   *
   * @param memberId 사용자 기능
   * @param amount   충전하려는 금액
   * @return 카카오 서버에서 응답한 정보를 return
   */
  public KakaoPayResponse prepareCharge(String memberId, int amount) {
    KakaoPayRequest request = KakaoPayRequest.makeRequest(
        // KakaoPayRequest 의 static 메서드를 이용해 요청생성
        kakaoPayCid,
        memberId,
        amount,
        APPROVAL_URL,
        CANCEL_URL,
        FAIL_URL);

    KakaoPayResponse response = webClient.post()// webClient 를 이용해 POST 요청
        .uri(KAKAOPAY_PATH + KAKAOPAY_PREPARE_PATH) // path 경로 설정
        .bodyValue(request).retrieve()
        .bodyToMono(KakaoPayResponse.class) // KakaoPayResponse class 정보로 응답 받기
        .block(); // block 메서드로 비동기 방식으로 작업 (카카오에서 응답 올때까지 대기)

    String redisKey = "kp: " + memberId; // Redis 에 Tid 임시 저장을 위한 Key 생성
    // TID 를 Redis 에 임시저장. 1분간 저장된다.
    redisTemplate.opsForValue().set(redisKey, response.getTid(), 1, TimeUnit.MINUTES);

    return response;
  }

  /**
   * 카카오페이 요청 승인을 받는 메서드입니다. (준비 -> 승인 2단계에 걸쳐 작업이 진행됨)
   *
   * @param memberId 사용자 ID
   * @param pgToken  카카오서버에서 발급받은 pg_token
   * @return 카카오서버에서 승인받은 TID 를 담은 객체 return
   */
  public KakaoPayApproveResponse approveCharge(String memberId, String pgToken) {
    String redisKey = "kp: " + memberId;
    String tid = redisTemplate.opsForValue().get(redisKey); // Key 로 TID 를 redis 에서 찾는다

    KakaoPayApproveResponse response = webClient.post() // webClient 를 이용해 POST 요청
        .uri(KAKAOPAY_PATH + KAKAOPAY_APPROVE_PATH)
        .bodyValue(KakaoPayApproveRequest.makeRequest( // 카카오페이 충전 승인 요청을 위한 요청 생성
            kakaoPayCid,
            tid,
            pgToken,
            memberId
        )).retrieve()
        // 요청에 대한 응답값을 KakaoPayApproveResponse 로 받아서 return
        .bodyToMono(KakaoPayApproveResponse.class)
        .block(); // 비동기 처리

    redisTemplate.delete(redisKey); // 결제 승인이 완료되었으니 redis 에서 삭제해준다.

    return response;
  }
}
