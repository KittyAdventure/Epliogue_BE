package com.team1.epilogue.gathering.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.gathering.dto.MeetingDto;
import com.team1.epilogue.gathering.entity.Meeting;
import com.team1.epilogue.gathering.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService {

  private final MeetingRepository meetingRepository;
  private final MemberRepository memberRepository;
  private final BookRepository bookRepository;
  // 오프라인 모임생성
  public MeetingDto createMeeting(MeetingDto meetingDto){
    Member member = memberRepository.findById(meetingDto.getMemberId())
        .orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다."));

    Book book = bookRepository.findById(meetingDto.getBookId())
        .orElseThrow(() -> new IllegalArgumentException("책이 존재하지 않습니다."));

    Meeting meeting = Meeting.builder()
        .book(book)
        .member(member)
        .title(meetingDto.getTitle())
        .content(meetingDto.getContent())
        .location(meetingDto.getLocation())
        .dateTime(meetingDto.getDateTime())
        .nowPeople(0)
        .build();


    Meeting saveMeeting = meetingRepository.save(meeting);
    return MeetingDto.fromEntity(saveMeeting);
  }

  //모임 수정
  public Meeting updateMeeting(Long id, MeetingDto meetingDto){
    Meeting meeting = meetingRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 모임이 없습니다."));

    Meeting updatedMeeting = Meeting.builder()
        .id(meeting.getId())
        .member(meeting.getMember())
        .book(meeting.getBook())
        .title(meetingDto.getTitle())
        .content(meetingDto.getContent())
        .location(meetingDto.getLocation())
        .dateTime(meetingDto.getDateTime())
        .build();

    log.info("수정된 내용 : " + updatedMeeting);
    return meetingRepository.save(updatedMeeting);
  }

}
