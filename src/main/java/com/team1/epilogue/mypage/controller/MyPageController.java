package com.team1.epilogue.mypage.controller;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.mypage.dto.MyPageCalendarResponse;
import com.team1.epilogue.mypage.dto.MyPageCommentsResponse;
import com.team1.epilogue.mypage.dto.MyPageReviewsResponse;
import com.team1.epilogue.mypage.service.MyPageService;
import java.util.List;
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
   * @param page 조회할 페이지 번호
   */
  @GetMapping("/api/mypage/comments")
  public ResponseEntity<MyPageCommentsResponse> getMyComments(Authentication authentication,
      @RequestParam int page) {
    Member member = (Member) authentication.getPrincipal();
    MyPageCommentsResponse myComments = myPageService.getMyComments(member, page);
    return ResponseEntity.ok(myComments);
  }

  /**
   * 마이 페이지 내부 "해당 유저의 리뷰 리스트 보기" 기능을 위한 메서드
   *
   * @param memberId 조회할 유저의 ID
   * @param page 페이지 번호
   */
  @GetMapping("/api/mypage/reviews")
  public ResponseEntity<MyPageReviewsResponse> getReviewsByMember(@RequestParam String memberId,@RequestParam int page) {
    MyPageReviewsResponse reviews = myPageService.getReviewsByMember(memberId, page);
    return ResponseEntity.ok(reviews);
  }
  @GetMapping("/api/mypage/calendar")
  public ResponseEntity<List<MyPageCalendarResponse>> getCalendar(@RequestParam String memberId,@RequestParam String date) {
    List<MyPageCalendarResponse> calendar = myPageService.getCalendar(memberId, date);
    return ResponseEntity.ok(calendar);
  }
}
