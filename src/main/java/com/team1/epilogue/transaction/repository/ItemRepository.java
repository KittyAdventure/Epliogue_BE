package com.team1.epilogue.transaction.repository;

import com.team1.epilogue.transaction.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
  Page<Item> findAll(Pageable pageable);

}
