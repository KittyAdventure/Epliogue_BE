package com.team1.epilogue.chat.controller;

import com.team1.epilogue.chat.entity.Participation;
import com.team1.epilogue.chat.service.ParticipationService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/meeting/chat")
public class ParticipationController {

  ParticipationService participationService;


  //사용자 채팅방 참여
  @PostMapping("/{roomId}/participates")
  public Mono<Participation> joinRoom(Long memberId, String roomId){
    return participationService.joinRoom(memberId, roomId);
  }

  //사용자 채팅방에서 나감
  @DeleteMapping("/{roomId}/leave")
  public Mono<Void> leaveRoom(@PathVariable String roomId, @RequestParam Long memberId) {
    return participationService.leaveRoom(memberId,roomId);
  }



}
