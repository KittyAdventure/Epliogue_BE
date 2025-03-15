package com.team1.epilogue.gathering.repository;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.gathering.entity.JoinMeeting;
import com.team1.epilogue.gathering.entity.Meeting;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JoinMeetingRepository extends JpaRepository<JoinMeeting, Long> {
  Optional<JoinMeeting> findByMemberAndMeeting(Member member, Meeting meeting);
  boolean existsByMemberAndMeeting(Member member, Meeting meeting);

  //특정 미팅의 참가자 수 조회
  long countByMeeting(Meeting meeting);

  //특정 미팅의 참가자 목록 조회
  @Query("SELECT jm FROM JoinMeeting jm JOIN FETCH jm.member WHERE jm.meeting = :meeting")
  List<JoinMeeting> findByMeetingWithMember(@Param("meeting") Meeting meeting);

  @EntityGraph(attributePaths = {"meeting","meeting.book"})
  Page<JoinMeeting> findAllByMember_LoginId(String memberLoginId,Pageable page);
}


