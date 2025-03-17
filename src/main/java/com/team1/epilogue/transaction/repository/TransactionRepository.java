package com.team1.epilogue.transaction.repository;

import com.team1.epilogue.transaction.entity.Transaction;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  Page<Transaction> findByDateTimeBetweenAndMemberId(LocalDateTime startDate, LocalDateTime endDate,Long memberId, Pageable page);
}
