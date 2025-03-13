package com.team1.epilogue.alarm.controller;

import com.team1.epilogue.alarm.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class AlarmController {

  private final AlarmService alarmService;

  /**
   * 댓글 알림을 받기 위해 SSE 구독하는 기능
   */
  @GetMapping("/api/subscribe/{memberId}")
  public ResponseEntity<SseEmitter> subscribe(@PathVariable String memberId) {
    SseEmitter sseEmitter = alarmService.makeSse(memberId);

    return ResponseEntity.ok(sseEmitter);
  }
}
