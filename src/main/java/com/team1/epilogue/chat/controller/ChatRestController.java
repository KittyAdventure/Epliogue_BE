package com.team1.epilogue.chat.controller;

import com.team1.epilogue.chat.dto.ChatRoomDto;
import com.team1.epilogue.chat.service.ChatRoomService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meeting")
public class ChatRestController {

  private final ChatRoomService chatRoomService;


  /**
   * 채팅방 생성
   * @param title 채팅방 제목 (책 제목 등)
   * @param memberId 채팅방 생성자의 사용자 ID
   * @return 생성된 채팅방 정보
   */
  @PostMapping("/chatrooms")
  public ResponseEntity<ChatRoomDto> createRoom(@RequestParam String title, @RequestParam Long memberId) {
    ChatRoomDto createdRoom = chatRoomService.createRoom(title, memberId);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
  }

  /**
   * 전체 채팅방 조회
   * @return 채팅방 목록
   */
  @GetMapping("/chatrooms")
  public ResponseEntity<List<ChatRoomDto>> getAllRooms(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    List<ChatRoomDto> chatRooms = chatRoomService.getAllRooms(page,size);
    return ResponseEntity.ok(chatRooms);
  }

}
