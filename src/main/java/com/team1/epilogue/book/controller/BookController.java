package com.team1.epilogue.book.controller;

import com.team1.epilogue.book.dto.BookDetailResponse;
import com.team1.epilogue.book.dto.BookInfoRequest;
import com.team1.epilogue.book.dto.BookMainPageDto;
import com.team1.epilogue.book.dto.BookSearchFilter;
import com.team1.epilogue.book.dto.NaverBookSearchResponse;
import com.team1.epilogue.book.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  /**
   * 책 제목으로 검색하는 메서드입니다.
   *
   * @return 네이버에서 온 응답값을 return
   */
  @GetMapping("/api/books")
  public ResponseEntity<NaverBookSearchResponse> searchBookInfo(@RequestParam String query,
      @RequestParam(defaultValue = "sim") String sort,
      @RequestParam(defaultValue = "10") int display, @RequestParam(defaultValue = "1") int start) {

    NaverBookSearchResponse naverBookSearchResponse = bookService.searchBookInfo(
        BookInfoRequest.builder()
            .start(start)
            .query(query)
            .display(display)
            .sort(sort)
            .build());
    return ResponseEntity.ok(naverBookSearchResponse);
  }

  /**
   * 책 제목 or ISBN 번호로 상세검색하는 메서드입니다.
   *
   * @return 네이버에서 온 응답값을 return
   */
  @GetMapping("/api/books/detail")
  public ResponseEntity<BookDetailResponse> getBookDetail(@RequestParam String query,
      @RequestParam String type,
      HttpServletRequest request) {
    BookDetailResponse bookDetail = bookService.getBookDetail(query, type, request.getHeader("Authorization"));
    return ResponseEntity.ok(bookDetail);
  }

  /**
   * 책 메인 페이지에 들어갈때 조회되는 정보를 return 하는 메서드입니다.
   */
  @GetMapping("/api/books/main-page")
  public ResponseEntity<BookMainPageDto> getBookMainPage(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "") String sort,
      @RequestParam(defaultValue = "") String chosung,
      @RequestParam(defaultValue = "0") int rating,
      @RequestParam(defaultValue = "") String startDate,
      @RequestParam(defaultValue = "") String endDate) {
    BookSearchFilter filter = BookSearchFilter.builder()
        .page(page)
        .sort(sort)
        .chosung(chosung)
        .rating(rating)
        .startDate(startDate)
        .endDate(endDate)
        .build();
    BookMainPageDto bookMainPage = bookService.getBookMainPage(filter);
    return ResponseEntity.ok(bookMainPage);
  }
}
