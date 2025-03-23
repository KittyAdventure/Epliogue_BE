package com.team1.epilogue.book.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookSearchFilter {
  private int page;
  private String sort;
  private String chosung;
  private Integer rating;
  private String startDate;
  private String endDate;
  private int size;
}
