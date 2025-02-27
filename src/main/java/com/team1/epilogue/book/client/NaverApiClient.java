package com.team1.epilogue.book.client;

import com.team1.epilogue.book.dto.BookInfoRequest;
import com.team1.epilogue.book.dto.NaverBookSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class NaverApiClient {

  private final WebClient webClient;
  @Value("${naver.cid}")
  private String cid; // 네이버 client ID
  @Value("${naver.api.key}")
  private String apikey; // 네이버 api 호출을 위한 key
  private final String NAVER_BASE_URL = "https://openapi.naver.com"; // 네이버 base url
  private final String NAVER_BOOK_SERACH_PATH = "/v1/search/book.json"; // 책검색 api 를 위한 path


  public NaverBookSearchResponse getBookInfoFromNaver(BookInfoRequest dto) {
    NaverBookSearchResponse response = webClient.get()
        .uri(NAVER_BASE_URL + NAVER_BOOK_SERACH_PATH + "?query=" + dto.getQuery() + "&display="
            + dto.getDisplay()
            + "&start=" + dto.getStart() + "&sort=" + dto.getSort())
        .header("X-Naver-Client-Id", cid)
        .header("X-Naver-Client-Secret", apikey)
        .retrieve()
        .bodyToMono(NaverBookSearchResponse.class)
        .block();// 동기 방식으로 작업

    log.info(response.toString());

    return response;
  }
}
