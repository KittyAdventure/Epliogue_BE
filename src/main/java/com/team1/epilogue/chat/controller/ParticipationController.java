package com.team1.epilogue.chat.controller;

import com.team1.epilogue.chat.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meeting/chat")
@RequiredArgsConstructor
public class ParticipationController {

  private final ParticipationService participationService;

  /**
   * 채팅방 참여
   * @param roomId 채팅방 ID
   * @param memberId 참여할 사용자의 사용자 ID
   * @return 참여 성공 여부
   */
  @PostMapping("/rooms/{roomId}/join")
  public ResponseEntity<Void> joinRoom(@PathVariable String roomId, @RequestParam Long memberId) {
    boolean joined = participationService.joinRoom(roomId, memberId);
    if (joined) {
      return ResponseEntity.status(HttpStatus.CREATED).build();
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * 채팅방에서 나가기
   * @param roomId 채팅방 ID
   * @param memberId 나갈 사용자의 사용자 ID
   */
  @DeleteMapping("/rooms/{roomId}/leave")
  public ResponseEntity<Void> leaveRoom(@PathVariable String roomId, @RequestParam Long memberId) {
    participationService.leaveRoom(roomId,memberId);
    return ResponseEntity.noContent().build();  // 204 No Content
  }



}
