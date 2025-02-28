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

  @GetMapping("/api/books")
  public ResponseEntity<?> searchBookInfo(@RequestBody BookInfoRequest dto) {
    NaverBookSearchResponse naverBookSearchResponse = bookService.searchBookInfo(dto);
    return ResponseEntity.ok(naverBookSearchResponse);
  }

  @GetMapping("/api/books/detail")
  public ResponseEntity<?> getBookDetail(@RequestBody BookDetailRequest dto) {
    BookDetailResponse bookDetail = bookService.getBookDetail(dto);
    return ResponseEntity.ok(bookDetail);

  }
}
