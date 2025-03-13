package com.team1.epilogue.alarm.service;

import com.team1.epilogue.alarm.dto.AlarmResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class AlarmService {

  private final Map<String, SseEmitter> activeEmitters = new ConcurrentHashMap<>();

  /**
   * SSE 알림을 보내는 기능
   * @param memberId 작성자 ID
   * @param reviewId 리뷰의 ID
   * @param message 댓글 내용을 담은 객체
   */
  public void sendNotification(String memberId, Long reviewId, String message) {
    SseEmitter emitter = activeEmitters.get(memberId);
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
        activeEmitters.remove(memberId);
      }
    }
  }

  /**
   * SSE 연결을 하는 부분 로그인할때 호출된다.
   */
  public SseEmitter makeSse(String memberId) {

    SseEmitter emitter = new SseEmitter(10 * 60 * 1000L); // 10분 유지
    activeEmitters.put(memberId, emitter);

    emitter.onCompletion(() -> activeEmitters.remove(memberId)); // 연결해제 되면 삭제
    emitter.onTimeout(() -> activeEmitters.remove(memberId)); // 시간 10분 지나면 삭제

    return emitter;
  }
}
