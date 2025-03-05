package com.team1.epilogue.gathering.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.gathering.dto.MeetingDto;
import com.team1.epilogue.gathering.entity.Meeting;
import com.team1.epilogue.gathering.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingService {

  private final MeetingRepository meetingRepository;
  private final MemberRepository memberRepository;
  
  // 오프라인 모임생성
  public MeetingDto createMeeting(MeetingDto meetingDto){
    Member member = memberRepository.findById(meetingDto.getMemberId())
        .orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다."));

    Meeting meeting = Meeting.builder()
        .member(member)
        .title(meetingDto.getTitle())
        .content(meetingDto.getContent())
        .location(meetingDto.getLocation())
        .dateTime(meetingDto.getDateTime())
        .nowPeople(0)
        .build();


    meetingRepository.save(meeting);
    return MeetingDto.fromEntity(meeting);
  }

}
