package com.team1.epilogue.transaction.entity;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.common.entity.BaseEntity;
import com.team1.epilogue.transaction.domain.TransactionDetail;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
@Table(name = "point_transaction")
public class Transaction extends BaseEntity {

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

}
