package com.team1.epilogue.chat.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.chat.dto.ChatMessageDto;
import com.team1.epilogue.chat.entity.ChatMessage;
import com.team1.epilogue.chat.entity.ChatRoom;
import com.team1.epilogue.chat.repository.ChatMessageRepository;
import com.team1.epilogue.chat.repository.ChatRoomRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

  @InjectMocks
  private ChatMessageService chatMessageService;

  @Mock
  private ChatMessageRepository chatMessageRepository;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Test
  @DisplayName("메세지 저장")
  void message_Save() {
    // given
    ChatMessageDto chatMessageDto = ChatMessageDto.builder()
        .id("1")
        .roomId("room1")
        .memberId(1L)
        .content("안녕하세요!")
        .build();

    ChatMessage chatMessage = new ChatMessage("1","room1",1L,"안녕하세요!");
    ChatMessage savedChatMessage = new ChatMessage("1","room1",1L,"안녕하세요!");

    // Mock 설정
    when(memberRepository.findById(1L)).thenReturn(Optional.of(new Member()));
    when(chatRoomRepository.findById("room1")).thenReturn(Optional.of(new ChatRoom()));
    when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(savedChatMessage);

    // when
    ChatMessageDto result = chatMessageService.saveMessage(chatMessageDto);

    // then
    assertNotNull(result.getId());
    assertEquals("room1", result.getRoomId());
    assertEquals(1L, result.getMemberId().longValue());
    assertEquals("안녕하세요!", result.getContent());

    verify(memberRepository, times(1)).findById(1L);
    verify(chatRoomRepository, times(1)).findById("room1");
    verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
  }


  @Test
  @DisplayName("채팅방 메세지 조회")
  void message_checks() {
    String roomId = "room1";

    List<ChatMessage> messages = List.of(
        new ChatMessage(new Object().toString(),"room1", 1L, "첫 번째 메시지"),
        new ChatMessage(new Object().toString(),"room2", 2L, "두 번째 메시지"),
        new ChatMessage(new Object().toString(),"room3", 3L, "세 번째 메시지"),
        new ChatMessage(new Object().toString(),"room4", 4L, "네 번째 메시지")
    );

    // Mock 설정 (최신순 반환)
    when(chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId)).thenReturn(messages);

    // when
    List<ChatMessageDto> result = chatMessageService.getMessagesByRoom(roomId);

    // then
    assertNotNull(result);
    assertEquals(4, result.size());
    assertEquals("첫 번째 메시지", result.get(0).getContent()); // 최신 메시지가 맨 앞에 와야 함
    assertEquals("두 번째 메시지", result.get(1).getContent());
    assertEquals("세 번째 메시지", result.get(2).getContent());
    assertEquals("네 번째 메시지", result.get(3).getContent());
    verify(chatMessageRepository, times(1)).findByRoomIdOrderByCreatedAtDesc(roomId);
  }


}
