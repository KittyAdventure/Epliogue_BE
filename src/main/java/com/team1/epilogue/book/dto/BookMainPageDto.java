package com.team1.epilogue.book.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookMainPageDto {
  private int page;
  private int totalPages;
  private List<BookMainPageDetail> books;

}
