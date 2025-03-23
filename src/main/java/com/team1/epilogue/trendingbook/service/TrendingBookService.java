package com.team1.epilogue.trendingbook.service;

import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.trendingbook.entity.BookDetailHistory;
import com.team1.epilogue.trendingbook.repository.TrendingBookRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrendingBookService {

  private final BookRepository bookRepository;
  private final TrendingBookRepository trendingBookRepository;

  private static List<Book> trendingBookList;

  /**
   * project 가 run 될때 한번 실행 최신순 10개의 책 데이터를 list 에 넣어준다.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void initTrendingBookList() {
    trendingBookList = bookRepository.findTop10ByOrderByCreatedAtDesc();
  }

  /**
   * 책 조회 결과를 DB 에 저장하는 기능
   *
   * @param bookId 상세조회한 책의 PK
   */
  public void insertTrendingBookHistory(String bookId) {
    trendingBookRepository.save(
        BookDetailHistory.builder()
            .bookId(bookId)
            .build()
    );
  }

  /**
   * 1시간 마다 인기 책 업데이트
   */
  @Scheduled(cron = "0 0 * * * *")
  private void updateTrendingBookList() {
    List<Book> newBookList = new ArrayList<>();
    // trendingBookRepository 에서 가장 조회가 많이된 책의 PK 10개를 가져온다.
    List<String> topViewedBooks = trendingBookRepository.findTopViewedBooks(PageRequest.of(0, 10));
    // 위에서 가져온 10개의 PK 로 Book Entity 를 가져온다.
    topViewedBooks.stream().forEach(
        data -> newBookList.add(bookRepository.findById(data).get()
        ));

    // trendingBookList 를 교체해준다.
    trendingBookList = newBookList;
    log.info("인기 책 리스트가 업데이트 되었습니다.");
  }

  /**
   * DB 내부 어제날짜 데이터들을 삭제하는 메서드
   */
  @Scheduled(cron = "0 0 12 * * ?")
  private void deleteYesterDayData() {
    // 금일 00:00 시의 LocalDatetime 을 생성한다.
    LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

    // 금일 00:00 시보다 더 빠른 데이터들을 삭제한다.(어제자 데이터들)
    trendingBookRepository.deleteByCreatedAtBefore(startOfDay);
    log.info("책 조회 테이블이 초기화 되었습니다.");
  }

  /**
   * 현재 저장되어있는 가장 조회가 많이된 10개의 책 목록을 가져옵니다.
   */
  public List<Book> getTrendingBookList() {
    return trendingBookList;
  }
}
