package com.team1.epilogue.chat.service.Impl;

import com.team1.epilogue.chat.entity.ChatRoom;
import com.team1.epilogue.chat.repository.ChatRoomRepository;
import com.team1.epilogue.chat.service.ChatRoomService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
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
  public Mono<ChatRoom> createRoom(String title) {
    //현재시간으로 채팅방을 생성.
    ChatRoom chatRoom = ChatRoom.builder()
        .title(title)
        .createAt(LocalDateTime.now())
        .build();

    return chatRoomRepository.save(chatRoom);
  }

  /**
   * 채팅방 조회 (ID기반)
   */
  @Override
  public Flux<ChatRoom> getChatRooms(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    return chatRoomRepository.findAllBy(pageable);
  }

  /**
   * 채팅방 삭제
   */
  @Override
  public Mono<Void> deleteRoom(String id){
    return chatRoomRepository.deleteById(id);
  }


}
