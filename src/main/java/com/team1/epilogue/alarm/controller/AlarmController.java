package com.team1.epilogue.alarm.controller;

import com.team1.epilogue.alarm.dto.AlarmResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class AlarmController {

  private final Map<Long, SseEmitter> activeEmitters = new ConcurrentHashMap<>();

  @GetMapping("/api/subscribe/{reviewId}/{memberId}")
  public SseEmitter subscribe(@PathVariable Long reviewId, @PathVariable Long memberId) {
    SseEmitter emitter = new SseEmitter(10 * 60 * 1000L); // 10분 유지
    activeEmitters.put(memberId, emitter);

    emitter.onCompletion(() -> activeEmitters.remove(memberId));
    emitter.onTimeout(() -> activeEmitters.remove(memberId));

    return emitter;
  }

  public void sendNotification(Long authorId, Long reviewId, String message) {
    SseEmitter emitter = activeEmitters.get(authorId);
    if (emitter != null) {
      try {
        // 리뷰 ID 와 댓글 내용을 담은 DTO 생성
        AlarmResponse data = AlarmResponse.builder()
            .reviewId(reviewId)
            .content(message)
            .build();

        // 구독 중인 사용자에게 알림을 전송
        // 이벤트 이름은 comment 로 설정하고, 생성된 DTO를 데이터로 전달
        emitter.send(SseEmitter.event().name("comment").data(data));
      } catch (IOException e) {
        emitter.complete();
        activeEmitters.remove(authorId);
      }
    }
  }
}
