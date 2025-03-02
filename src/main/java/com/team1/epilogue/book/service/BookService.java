package com.team1.epilogue.book.service;

import com.team1.epilogue.book.client.NaverApiClient;
import com.team1.epilogue.book.dto.BookDetailRequest;
import com.team1.epilogue.book.dto.BookDetailResponse;
import com.team1.epilogue.book.dto.SameAuthorBookTitleIsbn;
import com.team1.epilogue.book.dto.xml.BookDetailXMLResponse;
import com.team1.epilogue.book.dto.BookInfoRequest;
import com.team1.epilogue.book.dto.NaverBookSearchResponse;
import com.team1.epilogue.book.dto.xml.Item;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

  private final NaverApiClient naverApiClient;
  private final BookRepository bookRepository;

  @Value("${naver.base.url}")
  String naverUrl;

  /**
   * 책 제목으로 검색하는 메서드입니다.
   *
   * @param dto 책 정보를 담은 DTO
   * @return 네이버에서 온 응답값을 return
   */
  public NaverBookSearchResponse searchBookInfo(BookInfoRequest dto) {
    NaverBookSearchResponse response = naverApiClient.getBookInfoFromNaver(naverUrl, dto);
    return response;
  }

  /**
   * 책 제목 or ISBN 번호로 상세검색하는 메서드입니다.
   *
   * @param dto 책 제목 / ISBN 번호를 담은 DTO
   * @return 네이버에서 온 응답값을 return
   */
  public BookDetailResponse getBookDetail(BookDetailRequest dto) {
    Optional<Book> bookOpt; // repository 에서 가져올 Optional 객체
    Book book; // Optional 내부의 책 데이터

    if ("d_isbn".equals(dto.getType())) { // dto 의 Type 에 따라 다른 쿼리문 호출
      bookOpt = bookRepository.findById(dto.getQuery()); // 책 ISBN 으로 DB 조회
    } else {
      bookOpt = bookRepository.findByTitle(dto.getQuery()); // 책 제목으로 DB 조회
    }

    // Optional 내부에 Book 데이터가 존재하지 않는다면 Naver API 호출 -> DB 에 저장
    book = bookOpt.orElseGet(
        () -> {
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

          return insertBookInfo(build);
        }
    );
    List<SameAuthorBookTitleIsbn> dtoList = new ArrayList<>();
    List<Book> sameAuthorBooks = bookRepository.findAllByAuthor(book.getAuthor());
    sameAuthorBooks.stream().forEach( // DB 에서 가져온 책들의 정보를 DTO List 로 변환
        data -> {
          if (data.getId() != book.getId()) { // 같은 작가의 현재 가져온 책을 제외한 다른 책들을 가져온다.
            dtoList.add(
                SameAuthorBookTitleIsbn.builder().title(data.getTitle()).id(data.getId()).build());
          }
        }
    );

    // DTO 로 반환 형식에 맞춰 return
    BookDetailResponse build = BookDetailResponse.builder()
        .title(book.getTitle())
        .image(book.getCoverUrl())
        .author(book.getAuthor())
        .price(book.getPrice())
        .publisher(book.getPublisher())
        .description(book.getDescription())
        .pubDate(Objects.toString(book.getPubDate(), ""))
        .isbn(book.getId())
        .sameAuthor(dtoList)
        .build();

    return build;
  }

  /**
   * getBookDetail() 메서드의 return 값인 BookDetailResponse를 이용해 책 정보를 DB 에 저장합니다.
   *
   * @param dto 네이버에서 응답한 책 정보를 담은 DTO
   * @return 저장된 Book return
   */
  public Book insertBookInfo(BookDetailResponse dto) {

    Book book = Book.builder()
        .id(dto.getIsbn())
        .title(dto.getTitle())
        .author(dto.getAuthor())
        .price(dto.getPrice())
        .description(dto.getDescription())
        .avgRating(0.0)
        .coverUrl(dto.getImage())
        .publisher(dto.getPublisher())
        .pubDate(Optional.ofNullable(dto.getPubDate())
            .map(LocalDate::parse)
            .orElse(null)) // null이면 그대로 null 할당
        .build();

    return bookRepository.save(book);
  }
}
