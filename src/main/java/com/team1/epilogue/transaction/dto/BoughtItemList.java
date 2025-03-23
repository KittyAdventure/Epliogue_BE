package com.team1.epilogue.transaction.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoughtItemList {
  private int page;
  private int totalPages;
  private List<BoughtItemDetail> items;

}
