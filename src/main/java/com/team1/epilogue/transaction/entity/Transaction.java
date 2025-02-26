package com.team1.epilogue.transaction.entity;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.transaction.domain.TransactionDetail;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "point_transaction")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Member member;

  private int amount;

  private LocalDateTime dateTime;

  private int afterBalance;

  @Enumerated(EnumType.STRING)
  private TransactionDetail detail;

  private String tid;

  /**
   * 거래 정보가 DB 로 insert 될때 호출되는 메서드
   * 거래가 이루어진 날짜와 시간을 설정해준다.
   */
  @PrePersist
  private void transactionDateTimeInit() {
    this.dateTime = LocalDateTime.now();
  }

}
