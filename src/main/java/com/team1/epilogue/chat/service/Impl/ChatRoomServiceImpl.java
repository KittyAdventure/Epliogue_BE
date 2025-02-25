package com.team1.epilogue.chat.service.Impl;

import com.team1.epilogue.chat.domain.ChatRoom;
import com.team1.epilogue.chat.dto.ChatRoomDto;
import com.team1.epilogue.chat.repository.ChatRoomRepository;
import com.team1.epilogue.chat.service.ChatRoomService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;


  /**
   * 채팅방을 생성
   * 채팅방 생성시 책 이름과 동일함
   */
  @Override
  public Mono<ChatRoom> createRoom(Long bookId, String title) {
    //현재시간으로 채팅방을 생성.
    ChatRoom chatRoom = ChatRoom.builder()
        .bookId(bookId)
        .title(title)
        .createAt(LocalDateTime.now())
        .build();
    return chatRoomRepository.save(chatRoom);
  }

  /**
   * 채팅방 조회
   */
  public Mono<ChatRoom> findRoomByBookId(Long bookId) {
    return chatRoomRepository.findByBookId(bookId);
  }


}
