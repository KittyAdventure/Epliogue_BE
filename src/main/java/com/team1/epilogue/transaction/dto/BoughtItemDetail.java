package com.team1.epilogue.transaction.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoughtItemDetail {
  private Long itemId;
  private String name;
  private int price;
  private LocalDate buyDate;
}
