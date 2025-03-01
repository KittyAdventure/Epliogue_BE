package com.team1.epilogue.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.team1.epilogue.book.dto.BookDetailResponse;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
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
    assertEquals("데미안",captor.getValue().getTitle());
  }
}