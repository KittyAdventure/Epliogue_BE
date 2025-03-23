package com.team1.epilogue.gathering.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.gathering.dto.JoinMeetingRequestDto;
import com.team1.epilogue.gathering.dto.JoinMeetingResponseDto;
import com.team1.epilogue.gathering.entity.JoinMeeting;
import com.team1.epilogue.gathering.entity.Meeting;
import com.team1.epilogue.gathering.repository.JoinMeetingRepository;
import com.team1.epilogue.gathering.repository.MeetingRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class JoinMeetingServiceTest {

  @InjectMocks
  private JoinMeetingService joinMeetingService;

  @Mock
  private JoinMeetingRepository joinMeetingRepository;

  @Mock
  private MeetingRepository meetingRepository;

  @Mock
  private MemberRepository memberRepository;

  private Member testMember;
  private Meeting testMeeting;
  private CustomMemberDetails testMemberDetails;


  @BeforeEach
  void setUp() {
    testMember = Member.builder()
        .id(1L)
        .name("testUser")
        .loginId("testLoginId")
        .nickname("testNick")
        .birthDate(LocalDate.of(2000, 1, 1))
        .email("test@example.com")
        .phone("010-1234-5678")
        .profileUrl("http://example.com/profile.jpg")
        .build();

    testMeeting = Meeting.builder()
        .id(1L)
        .title("Test Meeting")
        .content("Test Description")
        .build();

    testMemberDetails = CustomMemberDetails.fromMember(testMember);

    // 불필요한 stubbing 제거
    when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
    when(meetingRepository.findById(1L)).thenReturn(Optional.of(testMeeting));
  }

  @Test
  @DisplayName("미팅 참가 테스트")
  void testJoinMeeting() {
    JoinMeetingRequestDto requestDto = new JoinMeetingRequestDto(testMeeting.getId());
    JoinMeeting joinMeeting = JoinMeeting.builder().member(testMember).meeting(testMeeting).build();
    when(joinMeetingRepository.save(any(JoinMeeting.class))).thenReturn(joinMeeting);

    JoinMeetingResponseDto responseDto = joinMeetingService.joinMeeting(testMemberDetails, requestDto);

    assertNotNull(responseDto);
    assertEquals(testMember.getId(), responseDto.getMemberId());
    assertEquals(testMeeting.getId(), responseDto.getMeetingId());
  }

  @Test
  @DisplayName("미팅 떠나기 테스트")
  void testLeaveMeeting() {
    JoinMeetingRequestDto requestDto = new JoinMeetingRequestDto(testMeeting.getId());
    JoinMeeting joinMeeting = JoinMeeting.builder().member(testMember).meeting(testMeeting).build();
    when(joinMeetingRepository.findByMemberAndMeeting(testMember, testMeeting)).thenReturn(Optional.of(joinMeeting));

    joinMeetingService.leaveMeeting(testMemberDetails, requestDto);

    verify(joinMeetingRepository, times(1)).delete(joinMeeting);
  }



}

