package com.team1.epilogue.chat.service.Impl;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.team1.epilogue.chat.domain.ChatRoom;
import com.team1.epilogue.chat.repository.ChatRoomRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@ExtendWith(MockitoExtension.class)
class ChatRoomServiceImplTest {

  @Mock
  private ChatRoomRepository chatroomRepository;

  @InjectMocks
  private ChatRoomServiceImpl chatRoomService;

  @Test
  @DisplayName("채팅방 생성 테스트!")
  void chatting_room_create() {
    String roomId = "rooom1234";
    Long bookId = 1L;
    String bookName = "자유 치유할수 없는 질병";

    ChatRoom mockRoom = ChatRoom.builder()
        .id(roomId)
        .bookId(bookId)
        .title(bookName)
        .createAt(LocalDateTime.now())
        .build();

    //Mocking : chatRoomrepository.save() 호출 시 가짜 chatRoom 반환
    when(chatroomRepository.save(any(ChatRoom.class))).thenReturn(Mono.just(mockRoom));

    // 서비스 메서드 호출
    Mono<ChatRoom> createdRoom = chatRoomService.createRoom(bookId, bookName);

    //then: 검증
    StepVerifier.create(createdRoom)
        .assertNext(room -> {
          assertEquals(roomId, room.getId());
          assertEquals(bookId, room.getBookId());
          assertEquals(bookName, room.getTitle());
        })
        .verifyComplete();

  }

}