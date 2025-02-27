package com.team1.epilogue.transaction.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransactionHistoryRequest {
  private LocalDate startDate;
  private LocalDate endDate;
  private int page;
  private int limit;

}
