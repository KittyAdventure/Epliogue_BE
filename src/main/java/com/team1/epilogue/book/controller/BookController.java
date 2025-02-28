package com.team1.epilogue.book.controller;

import com.team1.epilogue.book.dto.BookDetailRequest;
import com.team1.epilogue.book.dto.BookDetailResponse;
import com.team1.epilogue.book.dto.BookInfoRequest;
import com.team1.epilogue.book.dto.NaverBookSearchResponse;
import com.team1.epilogue.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  /**
   * 책 제목으로 검색하는 메서드입니다.
   *
   * @param dto 책 정보를 담은 DTO
   * @return 네이버에서 온 응답값을 return
   */
  @GetMapping("/api/books")
  public ResponseEntity<?> searchBookInfo(@RequestBody BookInfoRequest dto) {
    NaverBookSearchResponse naverBookSearchResponse = bookService.searchBookInfo(dto);
    return ResponseEntity.ok(naverBookSearchResponse);
  }

  /**
   * 책 제목 or ISBN 번호로 상세검색하는 메서드입니다.
   *
   * @param dto 책 제목 / ISBN 번호를 담은 DTO
   * @return 네이버에서 온 응답값을 return
   */
  @GetMapping("/api/books/detail")
  public ResponseEntity<?> getBookDetail(@RequestBody BookDetailRequest dto) {
    BookDetailResponse bookDetail = bookService.getBookDetail(dto);
    return ResponseEntity.ok(bookDetail);

  }
}
