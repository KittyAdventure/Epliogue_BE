package com.team1.epilogue.book.repository;

import com.team1.epilogue.book.dto.BookSearchFilter;
import com.team1.epilogue.book.entity.Book;
import org.springframework.data.domain.Page;

/**
 * Query DSL 을 사용하기 위한 CustomRepository
 */
public interface CustomBookRepository {

  Page<Book> findBooksWithFilter(BookSearchFilter filter);

}
