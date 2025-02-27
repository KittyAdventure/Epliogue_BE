package com.team1.epilogue.book.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.book.dto.NaverBookResponseDto;
import com.team1.epilogue.book.dto.NaverBookSearchResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class NaverApiClientTest {

  private NaverApiClient naverApiClient;


  private static MockWebServer mockWebServer;
  private ObjectMapper objectMapper;

  @BeforeAll
  static void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockWebServer.shutdown();
  }


  @Test
  @DisplayName("네이버 책 검색 호출 API 테스트")
  void getBookInfoFromNaver() throws Exception {
    //given
    List<NaverBookResponseDto> items = new ArrayList<>();
    items.add(NaverBookResponseDto.builder()
        .title("데미안")
        .author("헤르만 헤세")
        .price(10000)
        .isbn("114")
        .description("데미안 입니다.")
        .pubDate(LocalDate.now())
        .image("http://www.ww.ww/11")
        .build());
    items.add(NaverBookResponseDto.builder()
        .title("데미안2")
        .author("헤르만 헤세2")
        .price(10000)
        .isbn("1142")
        .description("데미안2 입니다.")
        .pubDate(LocalDate.now())
        .image("http://www.ww.ww/11")
        .build());

    String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());

    mockWebServer.enqueue(new MockResponse().setBody(
        objectMapper.writeValueAsString(NaverBookSearchResponse.builder()
            .total(2)
            .items(items).build())));

    //when
//    naverApiClient.

    //then
//    assertThat(response).isNotNull();
  }
}