package com.team1.epilogue.transaction.client;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.transaction.dto.KakaoPayApproveResponse;
import com.team1.epilogue.transaction.dto.KakaoPayResponse;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KakaoPayClientTest {

  @Mock
  private StringRedisTemplate redisTemplate;
  @InjectMocks
  private KakaoPayClient kakaoPayClient;
  private MockWebServer mockWebServer;
  private String mockWebServerUrl = "";
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() throws Exception {
    mockWebServer = new MockWebServer();
    objectMapper = new ObjectMapper();
    mockWebServer.start(); // mockWebServer 실행
    //port 번호가 매번 변하기 때문에 그때그때 설정해 주어야한다.
    mockWebServerUrl = String.format("http://localhost:%s", mockWebServer.getPort());
    kakaoPayClient = new KakaoPayClient(redisTemplate,
        WebClient.builder().baseUrl(mockWebServerUrl).build());

  }

  @AfterEach
  void tearDown() throws Exception {
    // test 가 끝나면 mockWebServer 를 닫아준다.
    mockWebServer.shutdown();
  }


  @Test
  @DisplayName("카카오페이 결제 준비 API 테스트")
  void prepareCharge() throws Exception {
    //give
    // Mock 객체로 주입받은 redisTemplate 내부의 opsForValue() 메서드 정의해주기.
    // 정의안해주면 NPE 등장
    ValueOperations<String, String> valueOps = mock(ValueOperations.class);
    when(redisTemplate.opsForValue()).thenReturn(valueOps);

    mockWebServer.enqueue( // MockWebServer 에 Enqueue() 메서드로 응답 값을 세팅해준다.
        new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json")
            .setBody(
                objectMapper.writeValueAsString(KakaoPayResponse.builder()
                    .tid("9999") // 응답값으로 주는 Tid 는 9999 가 날아올 것이다.
                    .nextRedirectPCUrl("http://www.dddd.dddd")
                    .build()
                )));

    //when
    KakaoPayResponse response = kakaoPayClient.prepareCharge(mockWebServerUrl, "subin", 1000);

    //then
    assertNotNull(response); // 응답값이 null 이 아닌지 검증
    assertEquals("9999", response.getTid()); // 응답값 내부의 Tid 가 9999 가 맞는지 검증
    // redirect Url 이 우리가 입력한 값과 맞는지 검증
    assertEquals("http://www.dddd.dddd", response.getNextRedirectPCUrl());
    // opsForValue 가 실행되었는지 검증
    verify(redisTemplate.opsForValue(), times(1)).set(eq("kp: subin"), eq("9999"), anyLong(), eq(
        TimeUnit.MINUTES));
  }

  @Test
  @DisplayName("카카오페이 결제 승인 API 테스트")
  void approveCharge() throws Exception {
    //give
    // Redis 내부 메서드들 정의해주기
    ValueOperations<String, String> valueOps = mock(ValueOperations.class);
    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(redisTemplate.delete(any(String.class))).thenReturn(true);  // true 값을 반환

    mockWebServer.enqueue( // MockWebServer 에 Enqueue() 메서드로 응답 값을 세팅해준다.
        new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json")
            .setBody(objectMapper.writeValueAsString(
                KakaoPayApproveResponse.builder().aid("1")
                    .tid("9999")
                    .build()
            ))
    );

    //when
    KakaoPayApproveResponse response = kakaoPayClient.approveCharge("","test", "test");

    //then
    assertEquals("9999", response.getTid());
    verify(redisTemplate, times(1)).delete(any(String.class));
  }

  @Test
  @DisplayName("카카오페이 환불 API 테스트")
  void refund() {


  }
}