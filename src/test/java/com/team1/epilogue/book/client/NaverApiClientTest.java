package com.team1.epilogue.book.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.team1.epilogue.book.dto.BookDetailRequest;
import com.team1.epilogue.book.dto.BookInfoRequest;
import com.team1.epilogue.book.dto.NaverBookResponseDto;
import com.team1.epilogue.book.dto.NaverBookSearchResponse;
import com.team1.epilogue.book.dto.xml.BookDetailXMLResponse;
import com.team1.epilogue.book.dto.xml.Item;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NaverApiClientTest {

  @InjectMocks
  private NaverApiClient naverApiClient;
  private MockWebServer mockWebServer;
  private ObjectMapper objectMapper;
  private String mockWebServerUrl = "";

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    mockWebServer.start(); // mockWebServer 실행
    //port 번호가 매번 변하기 때문에 그때그때 설정해 주어야한다.
    mockWebServerUrl = String.format("http://localhost:%s", mockWebServer.getPort());
    naverApiClient = new NaverApiClient(RestClient.builder().baseUrl(mockWebServerUrl).build());

    // @Value 필드 수동 설정
    ReflectionTestUtils.setField(naverApiClient, "cid", "test-client-id");
    ReflectionTestUtils.setField(naverApiClient, "apikey", "test-api-key");
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }


  @Test
  @DisplayName("네이버 책 검색 호출 API 테스트")
  void getBookInfoFromNaver() throws Exception {
    //given
    // 네이버 서버에서 오는 응답값 설정
    List<NaverBookResponseDto> items = new ArrayList<>();
    items.add(
        NaverBookResponseDto.builder()
            .title("데미안")
            .author("헤르만 헤세")
            .price(10000)
            .isbn("12344")
            .description("헤르만 헤세의 데미안입니다.")
            .pubDate(LocalDate.now())
            .image("http://ddd.ddd.ddd/데미안")
            .build()
    );
    items.add(
        NaverBookResponseDto.builder()
            .title("타이탄의 도구들")
            .author("팀 페리스")
            .price(20000)
            .isbn("33333")
            .description("타이탄의 도구들 입니다.")
            .pubDate(LocalDate.now())
            .image("http://ddd.ddd.ddd/타이탄")
            .build()
    );

    mockWebServer.enqueue( // enqueue 메서드로 응답값 설정
        new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json")
            .setBody(objectMapper.writeValueAsString(NaverBookSearchResponse.builder()
                .total(2)
                .items(items)
                .build()))
    );
    BookInfoRequest request = BookInfoRequest.builder().query("타이탄").sort("sim").display(10)
        .start(1).build();

    //when
    NaverBookSearchResponse response = naverApiClient
        .getBookInfoFromNaver(mockWebServerUrl, request);

    //then
    assertEquals(2, response.getTotal());
    assertEquals(2, response.getItems().size());
    assertEquals("타이탄의 도구들", response.getItems().get(1).getTitle());
  }

  @Test
  @DisplayName("네이버 책 상세정보 호출 API 테스트")
  void get_book_detail_from_naver() throws Exception {
    //given
    // 테스트를 위한 응답값 만들기
    List<Item> items = new ArrayList<>();
    Item build = Item.builder()
        .title("데미안")
        .link("http://naver.com/1113")
        .author("헤르만 헤세")
        .price(10000)
        .publisher("출판사")
        .pubDate("1999-01-01")
        .description("헤르만 헤세의 데미안입니다.")
        .image("http://dddd.ddd.dd")
        .isbn("11111111 111111").build();

    items.add(build);

    mockWebServer.enqueue( // enqueue 메서드로 응답값 설정
        new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json")
            .setBody(objectMapper.writeValueAsString(BookDetailXMLResponse.builder()
                .items(items) // 위에서 만든 응답값을 넣어준다.
                .build())));

    // 요청 값 생성
    BookDetailRequest request = BookDetailRequest.builder()
        .type("d_titl")
        .query("데미안")
        .build();

    //when
    BookDetailXMLResponse response = naverApiClient.getBookDetail("", request);

    //then
    assertEquals("데미안", response.getItems().get(0).getTitle());
    assertEquals("헤르만 헤세", response.getItems().get(0).getAuthor());
  }
}