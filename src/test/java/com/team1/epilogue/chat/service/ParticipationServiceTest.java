package com.team1.epilogue.chat.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.book.service.BookService;
import com.team1.epilogue.chat.dto.ChatMessageDto;
import com.team1.epilogue.chat.entity.ChatMessage;
import com.team1.epilogue.chat.entity.ChatRoom;
import com.team1.epilogue.chat.entity.Participation;
import com.team1.epilogue.chat.repository.ChatMessageRepository;
import com.team1.epilogue.chat.repository.ChatRoomRepository;
import com.team1.epilogue.chat.repository.ParticipationRepository;
import com.team1.epilogue.config.MongoConfig;
import java.util.Arrays;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;



@Slf4j
@ExtendWith(MockitoExtension.class)
@Import(MongoConfig.class) // Auditing 활성화
@ExtendWith(MockitoExtension.class)
class ParticipationServiceTest {

  @Mock
  private ChatMessageRepository chatMessageRepository;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private ChatRoomRepository chatRoomRepository;


  @Mock
  private ParticipationRepository participationRepository;

  @InjectMocks
  private ParticipationService participationService;

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
  void joinRoom_Success() {
    when(memberRepository.findById(1L)).thenReturn(Optional.of(new Member()));
    ChatRoom chatRoom = new ChatRoom("room1", "Test Room", 1, 1L);
    when(chatRoomRepository.findById("room1")).thenReturn(Optional.of(chatRoom));
    when(participationRepository.existsByRoomIdAndMemberId("room1", 1L)).thenReturn(false);
    when(participationRepository.save(any(Participation.class))).thenReturn(
        Participation.builder()
            .roomId("room1")
            .memberId(1L)
            .build()
    );

    boolean result = participationService.joinRoom("room1", 1L);

    assertTrue(result);
    verify(participationRepository, atLeastOnce()).save(any(Participation.class));
    verify(chatRoomRepository, times(1)).save(chatRoom);
  }

  @Test
  void leaveRoom_Success() {
    when(memberRepository.findById(1L)).thenReturn(Optional.of(new Member()));
    ChatRoom chatRoom = new ChatRoom("room1", "Test Room", 1, 1L);
    when(chatRoomRepository.findById("room1")).thenReturn(Optional.of(chatRoom));
    when(participationRepository.findByRoomId("room1")).thenReturn(
        Arrays.asList(Participation.builder()
            .roomId("room1")
            .memberId(1L)
            .build()));

    participationService.leaveRoom("room1", 1L);

    verify(participationRepository, times(1)).delete(any(Participation.class));
    verify(chatRoomRepository, times(1)).deleteById("room1");
  }
}
