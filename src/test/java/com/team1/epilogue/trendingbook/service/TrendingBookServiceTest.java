package com.team1.epilogue.trendingbook.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.trendingbook.entity.BookDetailHistory;
import com.team1.epilogue.trendingbook.repository.TrendingBookRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrendingBookServiceTest {

  @Mock
  private BookRepository bookRepository;

  @Mock
  private TrendingBookRepository trendingBookRepository;

  @InjectMocks
  private TrendingBookService trendingBookService;

  private List<Book> bookList;

  @BeforeEach
  void setUp() {
    // 초기의 조회수 Top 책 리스트 생성
    bookList = Arrays.asList(
        Book.builder().id("1").title("book1").build(),
        Book.builder().id("2").title("book2").build(),
        Book.builder().id("3").title("book3").build(),
        Book.builder().id("4").title("book4").build(),
        Book.builder().id("5").title("book5").build(),
        Book.builder().id("6").title("book6").build(),
        Book.builder().id("7").title("book7").build(),
        Book.builder().id("8").title("book8").build(),
        Book.builder().id("9").title("book9").build(),
        Book.builder().id("10").title("book10").build()
    );
  }

  @Test
  @DisplayName("초기 책 목록 설정 기능 테스트")
  void initTrendingBookList() {
    //given
    // 초기 책 목록 설정
    when(bookRepository.findTop10ByOrderByCreatedAtDesc()).thenReturn(bookList);

    //when
    // initTrendingBookList() 이 호출될때 책이 초기화 되는지 검증
    trendingBookService.initTrendingBookList();
    List<Book> list = trendingBookService.getTrendingBookList();

    //then
    assertEquals(10, list.size());
    assertEquals(bookList, list);
    verify(bookRepository,times(1)).findTop10ByOrderByCreatedAtDesc();
  }

  @Test
  @DisplayName("DB 에 책 조회 기록 삽입 테스트")
  void insertTrendingBookHistory() {
    //given
    String bookId = "11";
    BookDetailHistory newBook = BookDetailHistory.builder().bookId(bookId).build();

    //when
    trendingBookService.insertTrendingBookHistory(bookId);

    //then
    // ArgumentCaptor 를 이용해서 trendingBookRepository 에 save 된 객체를 가져온다.
    ArgumentCaptor<BookDetailHistory> captor = ArgumentCaptor.forClass(BookDetailHistory.class);
    verify(trendingBookRepository).save(captor.capture());

    BookDetailHistory saved = captor.getValue();
    assertEquals(saved.getBookId(),bookId);
  }
}