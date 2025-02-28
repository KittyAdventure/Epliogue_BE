package com.team1.epilogue.book.service;

import com.team1.epilogue.book.client.NaverApiClient;
import com.team1.epilogue.book.dto.BookDetailRequest;
import com.team1.epilogue.book.dto.BookDetailResponse;
import com.team1.epilogue.book.dto.xml.BookDetailXMLResponse;
import com.team1.epilogue.book.dto.BookInfoRequest;
import com.team1.epilogue.book.dto.NaverBookSearchResponse;
import com.team1.epilogue.book.dto.xml.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {
  private final NaverApiClient naverApiClient;

  @Value("${naver.base.url}")
  String naverUrl;

  /**
   * 책 제목으로 검색하는 메서드입니다.
   *
   * @param dto 책 정보를 담은 DTO
   * @return 네이버에서 온 응답값을 return
   */
  public NaverBookSearchResponse searchBookInfo(BookInfoRequest dto) {
    NaverBookSearchResponse response = naverApiClient.getBookInfoFromNaver(naverUrl,dto);
    return response;
  }

  /**
   * 책 제목 or ISBN 번호로 상세검색하는 메서드입니다.
   *
   * @param dto 책 제목 / ISBN 번호를 담은 DTO
   * @return 네이버에서 온 응답값을 return
   */
  public BookDetailResponse getBookDetail(BookDetailRequest dto) {
    BookDetailXMLResponse response = naverApiClient.getBookDetail(naverUrl, dto);

    Item item = response.getItems().get(0);

    // DTO 로 반환 형식에 맞춰 return
    BookDetailResponse build = BookDetailResponse.builder()
        .title(item.getTitle())
        .image(item.getImage())
        .author(item.getAuthor())
        .price(item.getPrice())
        .publisher(item.getPublisher())
        .description(item.getDescription())
        .pubDate(item.getPubDate())
        .isbn(item.getIsbn())
        .build();

    return build;
  }
}
