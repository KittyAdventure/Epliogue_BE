package com.team1.epilogue.transaction.service;

import static org.junit.jupiter.api.Assertions.*;

import com.team1.epilogue.transaction.dto.KakaoPayResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KakaoPayServiceTest {

  @Autowired
  private KakaoPayService kakaoPayService;

  @DisplayName("카카오페이 준비단계 API 테스트")
  @Test
  void prepareChargeTest() {
    // given
    String memberId = "test1";
    int amount = 10000;

    // when
    KakaoPayResponse response = kakaoPayService.prepareCharge(memberId, amount);

    // then
    assertNotNull(response.getTid());
  }

}