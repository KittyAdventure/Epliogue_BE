package com.team1.epilogue.gathering.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.gathering.dto.JoinMeetingRequestDto;
import com.team1.epilogue.gathering.dto.JoinMeetingResponseDto;
import com.team1.epilogue.gathering.entity.JoinMeeting;
import com.team1.epilogue.gathering.entity.Meeting;
import com.team1.epilogue.gathering.repository.JoinMeetingRepository;
import com.team1.epilogue.gathering.repository.MeetingRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JoinMeetingService {

  private final JoinMeetingRepository joinMeetingRepository;
  private final MeetingRepository meetingRepository;

  //미팅 참가
  @Transactional
  public JoinMeetingResponseDto joinMeeting(CustomMemberDetails memberDetails, JoinMeetingRequestDto requestDto) {
    Member member = memberDetails.getMember();

    Meeting meeting = meetingRepository.findById(requestDto.getMeetingId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 미팅입니다."));

    boolean alreadyJoin = joinMeetingRepository.existsByMemberAndMeeting(member, meeting);
    if(alreadyJoin) {
      throw new IllegalArgumentException("이미 참가한 미팅입니다.");
    }

    JoinMeeting joinMeeting = joinMeetingRepository.save(
        JoinMeeting.builder()
            .member(member)
            .meeting(meeting)
            .build()
    );

    return JoinMeetingResponseDto.fromEntity(joinMeeting);
  }

  //미팅 나가기
  public void leaveMeeting(CustomMemberDetails memberDetails, JoinMeetingRequestDto requestDto) {
    Member member = memberDetails.getMember();; // 로그인한 회원 정보 가져오기

    Meeting meeting = meetingRepository.findById(requestDto.getMeetingId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 미팅입니다."));

    JoinMeeting joinMeeting = joinMeetingRepository.findByMemberAndMeeting(member, meeting)
        .orElseThrow(() -> new IllegalStateException("미팅에 참가하지 않았습니다."));

    joinMeetingRepository.delete(joinMeeting);
  }


  // 참가 인원 수 조회
  public long getParticipantCount(Long meetingId) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 미팅입니다."));
    return joinMeetingRepository.countByMeeting(meeting);
  }

  // 참가자 목록 조회
  public List<JoinMeetingResponseDto> getParticipants(Long meetingId) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 미팅입니다."));

    List<JoinMeeting> participants = joinMeetingRepository.findByMeetingWithMember(meeting);
    return participants.stream()
        .map(JoinMeetingResponseDto::fromEntity)
        .collect(Collectors.toList());
  }



}
