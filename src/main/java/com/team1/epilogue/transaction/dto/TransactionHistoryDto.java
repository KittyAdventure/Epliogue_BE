package com.team1.epilogue.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team1.epilogue.transaction.entity.Transaction;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransactionHistoryDto {
  private Long transactionId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime dateTime;
  private int amount;

  public static TransactionHistoryDto toDto(Transaction transaction) {
    return TransactionHistoryDto.builder()
        .transactionId(transaction.getId())
        .dateTime(transaction.getDateTime())
        .amount(transaction.getAmount())
        .build();
  }
}
