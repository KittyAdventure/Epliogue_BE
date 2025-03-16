package com.team1.epilogue.transaction.repository;

import com.team1.epilogue.transaction.entity.ItemBuyHistory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemBuyHistoryRepository extends JpaRepository<ItemBuyHistory, Long> {

  boolean existsByItemIdAndMemberId(Long itemId, Long memberId);

  @EntityGraph(attributePaths = {"item","member"})
  Page<ItemBuyHistory> findAllByMemberId(Long memberId, PageRequest request);

  @Query("SELECT h.item.id FROM ItemBuyHistory h WHERE h.member.id = :memberId")
  List<Long> findAllItemIdsByMemberId(@Param("memberId") Long memberId);
}
