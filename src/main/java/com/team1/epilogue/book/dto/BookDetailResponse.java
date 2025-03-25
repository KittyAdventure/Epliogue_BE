package com.team1.epilogue.book.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookDetailResponse {
  private String title;
  private String image;
  private String author;
  private int price;
  private String publisher;
  private String description;
  private String pubDate;
  private String isbn;
  private double avgRating;
  private List<SameAuthorBookTitleIsbn> sameAuthor;
  private boolean existCollection;

}
