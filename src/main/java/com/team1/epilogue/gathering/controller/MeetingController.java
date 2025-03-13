package com.team1.epilogue.gathering.controller;


import com.team1.epilogue.gathering.dto.MeetingDto;
import com.team1.epilogue.gathering.entity.Meeting;
import com.team1.epilogue.gathering.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meetings/gatherings")
@RequiredArgsConstructor
public class MeetingController {

  private final MeetingService meetingService;

  //오프라인 모임 생성
  @PostMapping
  public ResponseEntity<MeetingDto> createMeeting(@RequestBody MeetingDto meetingDto){
    return ResponseEntity.ok(meetingService.createMeeting(meetingDto));
  }

  // 오프라인 모임 수정
  @PutMapping("/{id}")
  public ResponseEntity<Meeting> updateMeeting(@PathVariable Long id, @RequestBody MeetingDto dto) {
    return ResponseEntity.ok(meetingService.updateMeeting(id, dto));
  }

  // 오프라인 모임 조회
  @GetMapping
  public ResponseEntity<Page<MeetingDto>> getMeetings(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {

    Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").descending());
    Page<MeetingDto> meetingDtoPage = meetingService.getMeetings(pageable);

    return ResponseEntity.ok(meetingDtoPage);
  }

}
