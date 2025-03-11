package com.team1.epilogue.book.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team1.epilogue.book.dto.BookSearchFilter;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.entity.QBook;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.team1.epilogue.book.entity.QBook.book; // Q Entity 를 static import 한다

@Repository
@RequiredArgsConstructor
public class CustomBookRepositoryImpl implements CustomBookRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Page<Book> findBooksWithFilter(BookSearchFilter filter) {
    // PageRequest 생성
    Pageable pageable = PageRequest.of(filter.getPage() - 1, 9);

    BooleanBuilder booleanBuilder = new BooleanBuilder();

    // 초성 필터
    if (filter.getChosung() != null) {
      booleanBuilder.and(book.chosung.eq(filter.getChosung()));
    }
    // 별점 필터
    if (filter.getRating() != null) {
      int rating = filter.getRating();
      booleanBuilder.and(book.avgRating.goe((double) rating)) // avgRating >= rating
          .and(book.avgRating.lt((double) rating + 1)); // avgRating < rating + 1 (3.9 까지)
    }
    // 출간일 필터
    if (filter.getStartDate() != null && filter.getEndDate() != null) {
      // "yyyy-MM" 형식을 LocalDate로 변환
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

      // 시작 날짜: 해당 월의 첫 번째 날짜
      LocalDate startDate = LocalDate.parse(filter.getStartDate() + "-01", formatter);

      // 종료 날짜: 해당 월의 마지막 날짜
      LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

      booleanBuilder.and(book.pubDate.goe(startDate)) // pubDate >= startDate
          .and(book.pubDate.loe(endDate)); // pubDate <= endDate
    }

    // 총 갯수 구하기 (전체 데이터를 기준으로 count 쿼리 실행)
    long totalCount = jpaQueryFactory
        .selectFrom(book)
        .where(booleanBuilder)
        .fetchCount(); // 전체 개수를 구하는 메서드

    // 쿼리 날리기!
    List<Book> books = jpaQueryFactory
        .selectFrom(book)
        .where(booleanBuilder)
        .orderBy(getSortOrder(filter, book)) // 정렬 기준 설정
        .offset((filter.getPage() - 1) * 9L) // 페이징 처리
        .limit(9) // 9개씩
        .fetch();

    return new PageImpl<>(books, pageable,totalCount); // Page 객체로 return
  }

  /**
   * 정렬 기준 설정하는 메서드
   */
  private OrderSpecifier<?>[] getSortOrder(BookSearchFilter filter, QBook book) {
    List<OrderSpecifier<?>> orders = new ArrayList<>();

    switch (filter.getSort()) {
      case "rating": // 별점 내림차순
        orders.add(book.avgRating.desc());
        break;
      case "view": // 조회수 내림차순
        orders.add(book.view.desc());
        break;
      case "date": // 날짜 내림차순 = 최신순
        orders.add(book.pubDate.desc());
        break;
    }
    return orders.toArray(new OrderSpecifier[0]); // 배열로 변환하여 반환
  }
}
