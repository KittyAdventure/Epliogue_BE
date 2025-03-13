package com.team1.epilogue.trendingbook.repository;

import com.team1.epilogue.trendingbook.entity.BookDetailHistory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TrendingBookRepository extends JpaRepository<BookDetailHistory,Long> {

  // 조회수가 높은 책 ID를 가져오는 메서드
  @Query("SELECT h.bookId FROM BookDetailHistory h GROUP BY h.bookId ORDER BY COUNT(h.bookId) DESC")
  List<String> findTopViewedBooks(Pageable pageable);

  void deleteByCreatedAtBefore(LocalDateTime startOfToday);

}
