package com.team1.epilogue.book.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team1.epilogue.book.dto.BookSearchFilter;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.entity.QBook;
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

    List<Book> books = jpaQueryFactory
        .selectFrom(book)
        .orderBy(getSortOrder(filter, book)) // 정렬 기준 설정
        .offset(filter.getPage()) // 페이징 처리
        .limit(9) // 9개씩
        .fetch();

    return new PageImpl<>(books, pageable, books.size()); // Page 객체로 return
  }

  private OrderSpecifier<?>[] getSortOrder(BookSearchFilter filter, QBook book) {
    List<OrderSpecifier<?>> orders = new ArrayList<>();

    // 날짜 정렬 (createdAt)
    if (filter.isDateAsc()) { // true 일땐 날짜 오름차순으로
      orders.add(book.createdAt.asc());
    } else {
      orders.add(book.createdAt.desc());
    }

    // 별점 정렬 (rating)
    if (filter.isRatingAsc()) { // true 일땐 평점 오름차순으로
      orders.add(book.avgRating.asc());
    } else {
      orders.add(book.avgRating.desc());
    }

    return orders.toArray(new OrderSpecifier[0]); // 배열로 변환하여 반환
  }
}
