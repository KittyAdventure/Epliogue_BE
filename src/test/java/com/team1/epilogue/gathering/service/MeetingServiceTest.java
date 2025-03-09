package com.team1.epilogue.gathering.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.gathering.dto.MeetingDto;
import com.team1.epilogue.gathering.entity.Meeting;
import com.team1.epilogue.gathering.repository.MeetingRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
class MeetingServiceTest {


  ObjectMapper objectMapper = new ObjectMapper();
  @Mock
  private MeetingRepository meetingRepository;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private BookRepository bookRepository;

  @InjectMocks
  private MeetingService meetingService;

  @Test
  @DisplayName("오프라인 모임 생성")
  void create_Meeting() {
    //given
    Long memberId = 1L;
    Member member = new Member();
    member.setId(memberId);

    Book book = new Book();


    MeetingDto meetingDto = MeetingDto.builder()
        .memberId(memberId)
        .bookId("book1")
        .title("책 모임")
        .content("한달에 한권씩 읽으실분")
        .location("서울특별시 용산구 청파로47길 66 4층, 5층")
        .dateTime(LocalDateTime.of(2025, 3, 10, 15, 0))
        .nowPeople(0)
        .build();

    Meeting meeting = Meeting.builder()
        .member(member)
        .book(book)
        .title(meetingDto.getTitle())
        .content(meetingDto.getContent())
        .location(meetingDto.getLocation())
        .dateTime(meetingDto.getDateTime())
        .nowPeople(meetingDto.getNowPeople())
        .build();

    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
    when(bookRepository.findById("book1")).thenReturn(Optional.of(book));


    // when
    MeetingDto createdMeeting = meetingService.createMeeting(meetingDto);
    try {
      log.info("Created Meeting: {}", objectMapper.writeValueAsString(createdMeeting));
    } catch (JsonProcessingException e) {
      log.error("Error converting MeetingDto to JSON", e);
    }

    // then
    assertNotNull(createdMeeting);
    assertEquals(meetingDto.getTitle(), createdMeeting.getTitle());
    assertEquals(meetingDto.getContent(), createdMeeting.getContent());
    assertEquals(meetingDto.getLocation(), createdMeeting.getLocation());
    assertEquals(meetingDto.getDateTime(), createdMeeting.getDateTime());
    assertEquals(Integer.valueOf(0), createdMeeting.getNowPeople());
  }

  @Test
  void update_meeting() {
    //given
    Member member = Member.builder()
        .id(1L)
        .build();

    Book book = Book.builder()
        .id("book1")
        .build();

    Meeting meeting = Meeting.builder()
        .id(1L)
        .member(member)
        .book(book)
        .title("오래된 제목")
        .content("오래된 내용")
        .location("예전 주소")
        .dateTime(LocalDateTime.now())
        .nowPeople(5)
        .build();


    // 수정 된 제목
    MeetingDto meetingDto = MeetingDto.builder()
        .id(1L)
        .memberId(1L)
        .bookId("book1")
        .title("새로운 제목")
        .content("새로운 내용")
        .location("새로운 주소")
        .dateTime(LocalDateTime.now().plusDays(1))
        .nowPeople(10)
        .build();

    when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
    when(meetingRepository.save(any(Meeting.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    //when
    Meeting updatedMeeting = meetingService.updateMeeting(1L, meetingDto);
    try {
      log.info("Updated Meeting: {}", objectMapper.writeValueAsString(updatedMeeting));
    } catch (JsonProcessingException e) {
      log.error("Error converting Meeting to JSON", e);
    }

    // Then
    assertThat(updatedMeeting).isNotNull();
    assertThat(updatedMeeting.getTitle()).isEqualTo("새로운 제목");
    assertThat(updatedMeeting.getContent()).isEqualTo("새로운 내용");
    assertThat(updatedMeeting.getLocation()).isEqualTo("새로운 주소");
    assertThat(updatedMeeting.getDateTime()).isEqualTo(meetingDto.getDateTime());

  }
}