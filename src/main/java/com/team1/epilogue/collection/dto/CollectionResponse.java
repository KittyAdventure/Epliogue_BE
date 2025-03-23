package com.team1.epilogue.collection.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CollectionResponse {
  private int page;
  private int totalPages;
  List<CollectionDetail> books;

}
