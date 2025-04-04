package com.team1.epilogue.book.repository;

import com.team1.epilogue.book.entity.Book;
import java.util.List;
import java.util.Optional;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book,String>{

  Optional<Book> findByTitle(String title);

  List<Book> findAllByAuthor(String author);

  // 최신순 10개만 가져오기
  List<Book> findTop10ByOrderByCreatedAtDesc();

  @Modifying
  @Query("UPDATE Book b SET b.view = b.view + 1 WHERE b.id = :bookId")
  void increaseView(String bookId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT b FROM Book b WHERE b.id = :bookId")
  Optional<Book> findByIdWithLock(@Param("bookId") String bookId);
}
