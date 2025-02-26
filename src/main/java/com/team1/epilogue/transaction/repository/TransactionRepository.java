package com.team1.epilogue.transaction.repository;

import com.team1.epilogue.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
