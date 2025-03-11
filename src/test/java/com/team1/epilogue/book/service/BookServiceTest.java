package com.team1.epilogue.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.team1.epilogue.book.dto.BookDetailResponse;
import com.team1.epilogue.book.dto.BookMainPageDto;
import com.team1.epilogue.book.dto.BookSearchFilter;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.book.repository.CustomBookRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
  @Mock
  private CustomBookRepository customBookRepository;

  @Mock
  private BookRepository bookRepository;

  @InjectMocks
  private BookService bookService;

  @Test
  @DisplayName("책 정보 DB 저장 메서드 테스트")
  void insert_book_info() {
    //given
    Book testBook = Book
        .builder()
        .title("데미안")
        .author("헤르만헤세")
        .build();

    BookDetailResponse request = BookDetailResponse.builder()
        .title("데미안")
        .author("헤르만헤세")
        .build();

    when(bookRepository.save(any(Book.class))).thenReturn(testBook);

    // ArgumentCaptor 생성
    ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);

    //when
    Book book = bookService.insertBookInfo(request);

    //then
    assertEquals("데미안", book.getTitle());
    assertEquals("헤르만헤세", book.getAuthor());

    verify(bookRepository, times(1)).save(captor.capture());
    // ArgumentCaptor 를 이용해 저장된 객체를 검증한다.
    assertEquals("데미안", captor.getValue().getTitle());
  }

  @Test
  @DisplayName("QueryDSL 이용한 책 메인페이지 테스트")
  void getBookMainPage(){
    //given
    BookSearchFilter filter = BookSearchFilter.builder()
        .page(1)
        .dateAsc(true)
        .build();

    List<Book> list = new ArrayList<>();
    list.add(
        Book.builder()
            .id("1")
            .title("테스트1")
            .description("테스트책1 내용")
            .build()
    );
    list.add(
        Book.builder()
            .id("2")
            .title("테스트2")
            .description("테스트책2 내용")
            .build()
    );
    list.add(
        Book.builder()
            .id("3")
            .title("테스트3")
            .description("테스트책3 내용")
            .build()
    );
    PageRequest pageRequest = PageRequest.of(0, 9);
    Page<Book> page = new PageImpl<>(list, pageRequest, list.size());

    when(customBookRepository.findBooksWithFilter(filter)).thenReturn(page);

    //when
    BookMainPageDto bookMainPage = bookService.getBookMainPage(filter);

    //then
    assertEquals(1, bookMainPage.getTotalPages());
    assertEquals("테스트3", bookMainPage.getBooks().get(2).getBookTitle());
  }
}