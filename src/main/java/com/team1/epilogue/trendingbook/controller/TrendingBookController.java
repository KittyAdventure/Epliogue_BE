package com.team1.epilogue.trendingbook.controller;

import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.trendingbook.service.TrendingBookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TrendingBookController {
  private final TrendingBookService trendingBookService;

  @GetMapping("/api/trending-books")
  public ResponseEntity<List<Book>> getTrendingBookList() {
    List<Book> bookList = trendingBookService.getTrendingBookList();
    return ResponseEntity.ok(bookList);
  }
}
