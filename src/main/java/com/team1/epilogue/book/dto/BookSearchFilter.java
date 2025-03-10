package com.team1.epilogue.book.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookSearchFilter {
  private int page;
  private boolean dateAsc; // 날짜 필터. true 일땐 오름차순
  private boolean ratingAsc; // 별점 필터. true 일땐 오름차순
}
