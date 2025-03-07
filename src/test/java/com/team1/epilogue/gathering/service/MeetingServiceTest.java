package com.team1.epilogue.gathering.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.gathering.dto.MeetingDto;
import com.team1.epilogue.gathering.entity.Meeting;
import com.team1.epilogue.gathering.repository.MeetingRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

  @Mock
  private MeetingRepository meetingRepository;

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private MeetingService meetingService;

  @Test
  @DisplayName("오프라인 모임 생성")
  void create_Meeting() {
    //given
    Long memberId = 1L;
    Member member = new Member();
    member.setId(memberId);

    MeetingDto meetingDto = MeetingDto.builder()
        .memberId(memberId)
        .title("책 모임")
        .content("한달에 한권씩 읽으실분")
        .location("서울특별시 용산구 청파로47길 66 4층, 5층")
        .dateTime(LocalDateTime.of(2025,3,10,15,0))
        .nowPeople(0)
        .build();

    Meeting meeting = Meeting.builder()
        .member(member)
        .title(meetingDto.getTitle())
        .content(meetingDto.getContent())
        .location(meetingDto.getLocation())
        .dateTime(meetingDto.getDateTime())
        .nowPeople(meetingDto.getNowPeople())
        .build();

    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);

    // when
    MeetingDto createdMeeting = meetingService.createMeeting(meetingDto);

    // then
    assertNotNull(createdMeeting);
    assertEquals(meetingDto.getTitle(), createdMeeting.getTitle());
    assertEquals(meetingDto.getContent(), createdMeeting.getContent());
    assertEquals(meetingDto.getLocation(), createdMeeting.getLocation());
    assertEquals(meetingDto.getDateTime(), createdMeeting.getDateTime());
    assertEquals(Integer.valueOf(0), createdMeeting.getNowPeople());
  }


}