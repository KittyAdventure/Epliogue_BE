package com.team1.epilogue.book.service;

import com.team1.epilogue.book.client.NaverApiClient;
import com.team1.epilogue.book.dto.BookInfoRequest;
import com.team1.epilogue.book.dto.NaverBookSearchResponse;
import com.team1.epilogue.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {
  private final BookRepository bookRepository;
  private final NaverApiClient naverApiClient;

  public NaverBookSearchResponse searchBookInfo(BookInfoRequest dto) {
    NaverBookSearchResponse response = naverApiClient.getBookInfoFromNaver(dto);
    return response;
  }
}
