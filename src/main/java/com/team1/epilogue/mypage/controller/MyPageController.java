package com.team1.epilogue.mypage.controller;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.mypage.dto.MyPageCalendarResponse;
import com.team1.epilogue.mypage.dto.MyPageCommentsResponse;
import com.team1.epilogue.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyPageController {

  private final MyPageService myPageService;

  /**
   * 마이 페이지 내부 "내 댓글 보기" 기능을 위한 메서드
   *
   * @param authentication 인증된 사용자 정보를 담은 Authentication 객체
   * @param page           조회할 페이지 번호
   */
  @GetMapping("/api/mypage/comments")
  public ResponseEntity<MyPageCommentsResponse> getMyComments(Authentication authentication,
      @RequestParam int page) {
    Member member = (Member) authentication.getPrincipal();
    MyPageCommentsResponse myComments = myPageService.getMyComments(member, page);
    return ResponseEntity.ok(myComments);
  }

  @GetMapping("/api/mypage/calendar")
  public ResponseEntity<MyPageCalendarResponse> getCalendar(@RequestParam String memberId,@RequestParam String date) {
    myPageService.getCalendar(memberId,date);

  }
}
