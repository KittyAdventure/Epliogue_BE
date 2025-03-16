package com.team1.epilogue.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemListDetail {
  private int id;
  private String name;
  private int price;
  private boolean buy;
}
