package com.team1.epilogue.book.repository;

import com.team1.epilogue.book.entity.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book,String>{

  Optional<Book> findByTitle(String title);

  List<Book> findAllByAuthor(String author);

}
