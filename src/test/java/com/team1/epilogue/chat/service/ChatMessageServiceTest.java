package com.team1.epilogue.chat.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.chat.dto.ChatMessageDto;
import com.team1.epilogue.chat.entity.ChatMessage;
import com.team1.epilogue.chat.entity.ChatRoom;
import com.team1.epilogue.chat.repository.ChatMessageRepository;
import com.team1.epilogue.chat.repository.ChatRoomRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

  @Mock
  private ChatMessageRepository chatMessageRepository;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @InjectMocks
  private ChatMessageService chatMessageService;

  private ChatMessageDto chatMessageDto;
  private ChatMessage chatMessage;

  @BeforeEach
  void setUp() {
    chatMessageDto = ChatMessageDto.builder()
        .id("1")
        .roomId("room1")
        .memberId(1L)
        .content("Hello, world!")
        .build();

    chatMessage = new ChatMessage("1", "room1", 1L, "Hello, world!");
  }

  @Test
  void saveMessage_Success() {
    when(memberRepository.findById(1L)).thenReturn(Optional.of(new Member()));
    when(chatRoomRepository.findById("room1")).thenReturn(Optional.of(new ChatRoom()));
    when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(chatMessage);

    ChatMessageDto result = chatMessageService.saveMessage(chatMessageDto);

    assertNotNull(result);
    assertEquals("1", result.getId());
    assertEquals("room1", result.getRoomId());
    assertEquals(Long.valueOf(1L), result.getMemberId());
    assertEquals("Hello, world!", result.getContent());
  }

  @Test
  void saveMessage_MemberNotFound() {
    when(memberRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(MemberNotFoundException.class, () -> {
      chatMessageService.saveMessage(chatMessageDto);
    });
  }

  @Test
  void saveMessage_ChatRoomNotFound() {
    when(memberRepository.findById(1L)).thenReturn(Optional.of(new Member()));
    when(chatRoomRepository.findById("room1")).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> {
      chatMessageService.saveMessage(chatMessageDto);
    });
  }

  @Test
  void getMessagesByRoom_Success() {
    when(chatMessageRepository.findByRoomIdOrderByCreatedAtDesc("room1"))
        .thenReturn(Arrays.asList(chatMessage));

    List<ChatMessageDto> messages = chatMessageService.getMessagesByRoom("room1");

    assertNotNull(messages);
    assertEquals(1, messages.size());
    assertEquals("1", messages.get(0).getId());
    assertEquals("room1", messages.get(0).getRoomId());
    assertEquals(Long.valueOf(1L), messages.get(0).getMemberId());
    assertEquals("Hello, world!", messages.get(0).getContent());
  }
}
