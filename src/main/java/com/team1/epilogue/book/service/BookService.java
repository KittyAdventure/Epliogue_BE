package com.team1.epilogue.book.service;

import com.team1.epilogue.auth.security.JwtTokenProvider;
import com.team1.epilogue.book.client.NaverApiClient;
import com.team1.epilogue.book.dto.BookDetailRequest;
import com.team1.epilogue.book.dto.BookDetailResponse;
import com.team1.epilogue.book.dto.BookMainPageDetail;
import com.team1.epilogue.book.dto.BookMainPageDto;
import com.team1.epilogue.book.dto.BookSearchFilter;
import com.team1.epilogue.book.dto.SameAuthorBookTitleIsbn;
import com.team1.epilogue.book.dto.xml.BookDetailXMLResponse;
import com.team1.epilogue.book.dto.BookInfoRequest;
import com.team1.epilogue.book.dto.NaverBookSearchResponse;
import com.team1.epilogue.book.dto.xml.Item;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.book.repository.CustomBookRepository;
import com.team1.epilogue.collection.repository.CollectionRepository;
import com.team1.epilogue.keyword.service.KeyWordService;
import com.team1.epilogue.rating.repository.RatingRepository;
import com.team1.epilogue.trendingbook.service.TrendingBookService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

  private final NaverApiClient naverApiClient;
  private final BookRepository bookRepository;
  private final KeyWordService keyWordService;
  private final TrendingBookService trendingBookService;
  private final CustomBookRepository customBookRepository;
  private final RatingRepository ratingRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final CollectionRepository collectionRepository;

  @Value("${naver.base.url}")
  String naverUrl;

  /**
   * 책 제목으로 검색하는 메서드입니다.
   *
   * @param dto 책 정보를 담은 DTO
   * @return 네이버에서 온 응답값을 return
   */
  public NaverBookSearchResponse searchBookInfo(BookInfoRequest dto) {
    // 인기 검색어 기능을 위한 검색어 저장
    keyWordService.saveKeyWord(dto.getQuery());
    NaverBookSearchResponse response = naverApiClient.getBookInfoFromNaver(naverUrl, dto);
    return response;
  }

  /**
   * 책 제목 or ISBN 번호로 상세검색하는 메서드입니다.
   *
   * @return 네이버에서 온 응답값을 return
   */
  @Transactional
  public BookDetailResponse getBookDetail(String query, String type, String jwt) {
    Optional<Book> bookOpt; // repository 에서 가져올 Optional 객체
    Book book; // Optional 내부의 책 데이터
    boolean existCollection = false;

    if ("d_isbn".equals(type)) { // dto 의 Type 에 따라 다른 쿼리문 호출
      bookOpt = bookRepository.findById(query); // 책 ISBN 으로 DB 조회
    } else {
      bookOpt = bookRepository.findByTitle(query); // 책 제목으로 DB 조회
    }

    // Optional 내부에 Book 데이터가 존재하지 않는다면 Naver API 호출 -> DB 에 저장
    book = bookOpt.orElseGet(
        () -> {
          BookDetailXMLResponse response = naverApiClient.getBookDetail(naverUrl,
              BookDetailRequest.builder()
                  .query(query).type(type).build());

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
              .avgRating(ratingRepository.findAverageRatingByBookId(item.getIsbn()))
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
                SameAuthorBookTitleIsbn.builder().title(data.getTitle()).isbn(data.getId())
                    .build());
          }
        }
    );

    // 인기 책 목록 기능을 위해 DB 에 조회기록 저장
    trendingBookService.insertTrendingBookHistory(book.getId());

    bookRepository.increaseView(book.getId());// 조회수 ++

    Double avgRating = ratingRepository.findAverageRatingByBookId(book.getId());

    // 현재 유저가 해당 책을 좋아요 했는지 체크하는 부분
    if (jwt != null && jwt.startsWith("Bearer ")) {
      String token = jwt.substring(7);
      String memberIdFromJWT = jwtTokenProvider.getMemberIdFromJWT(token);
      existCollection = collectionRepository.existsByMember_IdAndBook_Id(
          Long.parseLong(memberIdFromJWT),
          book.getId());
    }

    // DTO 로 반환 형식에 맞춰 return
    BookDetailResponse build = BookDetailResponse.builder()
        .title(book.getTitle())
        .image(book.getCoverUrl())
        .author(book.getAuthor())
        .price(book.getPrice())
        .publisher(book.getPublisher())
        .description(book.getDescription())
        .existCollection(existCollection)
        .pubDate(Objects.toString(book.getPubDate(), ""))
        .isbn(book.getId())
        .avgRating(avgRating != null ? avgRating : 0.0)
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
        .avgRating(dto.getAvgRating())
        .coverUrl(dto.getImage())
        .publisher(dto.getPublisher())
        .pubDate(Optional.ofNullable(dto.getPubDate())
            .map(LocalDate::parse)
            .orElse(null)) // null이면 그대로 null 할당
        .chosung(getChosung(dto.getTitle())) // 이 책이 어떤 초성으로 시작하는지 설정
        .build();

    return bookRepository.save(book);
  }

  /**
   * QueryDSL 을 이용한 CustomRepository 에서 Book 데이터들을 가져옵니다.
   */
  public BookMainPageDto getBookMainPage(BookSearchFilter filter) {
    Page<Book> books = customBookRepository.findBooksWithFilter(filter);

    List<BookMainPageDetail> list = new ArrayList<>();

    books.stream().forEach(
        data -> {
          list.add(BookMainPageDetail.builder()
              .bookId(data.getId())
              .bookTitle(data.getTitle())
              .thumbnail(data.getCoverUrl())
              .build());
        }
    );

    return BookMainPageDto.builder()
        .page(filter.getPage())
        .totalPages(books.getTotalPages())
        .books(list)
        .build();
  }

  // 책 제목 첫글자에서 초성 따는 메서드
  private String getChosung(String title) {
    if (title == null || title.isEmpty()) {
      return "";
    }

    char firstChar = title.charAt(0);

    if (firstChar >= '가' && firstChar <= '힣') {
      // 한글 초성 추출
      int unicode = firstChar - 0xAC00;
      int chosungIndex = unicode / (21 * 28);
      final char[] CHOSUNG_LIST = {
          'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ',
          'ㅎ'
      };
      return String.valueOf(CHOSUNG_LIST[chosungIndex]);
    } else if ((firstChar >= 'A' && firstChar <= 'Z') || (firstChar >= 'a' && firstChar <= 'z')) {
      // 영문자는 대문자로 변환
      return String.valueOf(Character.toUpperCase(firstChar));
    } else {
      // 그 외 문자(숫자, 특수문자 등)는 그대로 반환
      return String.valueOf(firstChar);
    }
  }
}
