package com.team1.epilogue.book.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SameAuthorBookTitleIsbn {
  private String isbn;
  private String title;

}
