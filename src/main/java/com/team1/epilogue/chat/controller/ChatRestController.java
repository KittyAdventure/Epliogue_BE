package com.team1.epilogue.chat.controller;

import com.team1.epilogue.chat.entity.ChatRoom;
import com.team1.epilogue.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meeting")
public class ChatRestController {

  private final ChatRoomService chatRoomService;

  /**
   * 채팅방 = 책이름 생성
   */
  @PostMapping("/chatroom")
  public Mono<ChatRoom> createChatRoom(@RequestParam String title){
    return chatRoomService.createRoom(title);
  }

  /**
   *  채팅방 목록 조회 API
   *  페이징 방식.
   */
   @GetMapping("/chatrooms")
   public Flux<ChatRoom> getChatRoom(
       @RequestParam(defaultValue = "1") int page,
       @RequestParam(defaultValue = "10") int limit) {
     return chatRoomService.getChatRooms(page,limit);
   }

  /**
   *  채팅방 삭제 API
   */
  @DeleteMapping("/{id}")
  public Mono<Void> deleteRoom(@PathVariable String id){
    return chatRoomService.deleteRoom(id);
  }

}
