package com.team1.epilogue.collection.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CollectionDetail {
  private String bookId;
  private String bookTitle;
  private String thumbnail;

}
