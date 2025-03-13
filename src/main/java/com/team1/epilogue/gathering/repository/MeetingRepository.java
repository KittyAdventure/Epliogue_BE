package com.team1.epilogue.gathering.repository;

import com.team1.epilogue.gathering.entity.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MeetingRepository extends JpaRepository<Meeting,Long> {
  @Query("SELECT m FROM Meeting m JOIN FETCH m.member JOIN FETCH m.book")
  Page<Meeting> findAllWithDetails(Pageable pageable);
}
