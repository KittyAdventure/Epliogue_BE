package com.team1.epilogue.transaction.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransactionHistoryResponse {
  private int total;
  private int limit;
  private int page;
  private List<TransactionHistoryDto> data;
}
