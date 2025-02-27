package com.team1.epilogue.book.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NaverBookSearchResponse {
  private int total;
  @JsonProperty("items")
  private List<NaverBookResponseDto> items;
}
