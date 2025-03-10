package com.team1.epilogue.book.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookMainPageDetail {
  private String thumbnail;
  private String bookTitle;
  private String bookId;

}
