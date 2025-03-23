package com.team1.epilogue.gathering.repository;

import com.team1.epilogue.gathering.entity.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingRepository extends JpaRepository<Meeting,Long> {
  @Query("SELECT m FROM Meeting m JOIN FETCH m.member JOIN FETCH m.book")
  Page<Meeting> findAllWithDetails(Pageable pageable);

  @Modifying
  @Query("UPDATE Meeting m SET m.nowPeople = m.nowPeople + 1 WHERE m.id = :meetingId")
  void incrementNowPeople(@Param("meetingId") Long meetingId);

  @Modifying
  @Query("UPDATE Meeting m SET m.nowPeople = CASE WHEN m.nowPeople > 0 THEN m.nowPeople - 1 ELSE 0 END WHERE m.id = :meetingId")
  void decrementNowPeople(@Param("meetingId") Long meetingId);
}
