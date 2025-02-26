package com.team1.epilogue.chat.controller;

import com.team1.epilogue.chat.entity.ChatRoom;
import com.team1.epilogue.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meeting")
public class ChatRestController {

  private final ChatRoomService chatRoomService;

  /**
   * 책 ID를 기반으로 채팅방 = 책이름 생성
   */
  @PostMapping("/chatroom")
  public Mono<ChatRoom> createChatRoom(@RequestParam Long bookId, @RequestParam String title){
    return chatRoomService.createRoom(bookId, title);
  }

}
