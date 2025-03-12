package com.team1.epilogue.gathering.repository;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.gathering.entity.JoinMeeting;
import com.team1.epilogue.gathering.entity.Meeting;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinMeetingRepository extends JpaRepository<JoinMeeting, Long> {
  Optional<JoinMeeting> findByMemberAndMeeting(Member member, Meeting meeting);
  boolean existsByMemberAndMeeting(Member member, Meeting meeting);

  //특정 미팅의 참가자 수 조회
  long countByMeeting(Meeting meeting);

  //특정 미팅의 참가자 목록 조회
  List<JoinMeeting> findByMeeting(Meeting meeting);
}


