package com.team1.epilogue.book.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NaverBookResponseDto {
  private String title;
  private String author;
  private int price;
  private String isbn;
  private String description;
  private LocalDate pubDate;
  private String image;

}
