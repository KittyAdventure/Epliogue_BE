package com.team1.epilogue.gathering.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.gathering.dto.MeetingDto;
import com.team1.epilogue.gathering.entity.Meeting;
import com.team1.epilogue.gathering.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService {

  private final MeetingRepository meetingRepository;
  private final MemberRepository memberRepository;
  private final BookRepository bookRepository;
  // 오프라인 모임생성
  @Transactional
  public MeetingDto createMeeting(CustomMemberDetails memberDetails,MeetingDto meetingDto){
    Member member = memberRepository.findById(memberDetails.getId())
            .orElseThrow(() -> new MemberNotFoundException("ID가 " + memberDetails.getId() + "인 회원을 찾을 수 없습니다."));

    Book book = bookRepository.findById(meetingDto.getBookId())
        .orElseThrow(() -> new IllegalArgumentException("책이 존재하지 않습니다."));

    if(meetingDto.getMaxPeople() != null && meetingDto.getMaxPeople() > 30){
      throw new IllegalArgumentException("최대 인원 30명을 초과 할 수 없습니다.");
    }

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
  @Transactional
  public Meeting updateMeeting(CustomMemberDetails memberDetails,Long id, MeetingDto meetingDto){
    Long memberId = memberDetails.getId();
    Meeting meeting = meetingRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 모임이 없습니다."));

    // 현재 로그인한 사용자가 모임 주최자인지 확인
    if (!meeting.getMember().getId().equals(memberId)) {
      throw new IllegalArgumentException("모임을 수정할 권한이 없습니다.");
    }

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

  // 미팅 모임 삭제
  public void deleteMeeting(CustomMemberDetails memberDetails, Long meetingId){
    Long memberId = memberDetails.getId();

    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new IllegalArgumentException("해당 모임이 존재하지 않습니다."));

    // 현재 로그인한 사용자가 모임 주최자인지 확인
    if (!meeting.getMember().getId().equals(memberId)) {
      throw new IllegalArgumentException("모임을 삭제할 권한이 없습니다.");
    }

    meetingRepository.delete(meeting);
  }


  //모임 조회
  public Page<MeetingDto> getMeetings(Pageable pageable) {
    Page<Meeting> meetingPage = meetingRepository.findAllWithDetails(pageable);
    return meetingPage.map(MeetingDto::fromEntity);
  }
}
