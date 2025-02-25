package com.team1.epilogue.chat.service.Impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.team1.epilogue.chat.domain.ChatMessage;
import com.team1.epilogue.chat.repository.ChatMessageRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceImplTest {

  @Mock
  private ChatMessageRepository chatMessageRepository;  // Mock 객체

  @InjectMocks
  private ChatMessageServiceImpl chatMessageService; // 테스트 서비스


  @Test
  void chatting_message_save_test() {
    //given : 테스트에 사용할 Mock 데이터
    String messageId = "msg1234";
    String roomId = "room123";
    Long bookId = 123L;
    Long memberId = 12L;
    String content = "안녕하세요! 반갑습니다!.";

    ChatMessage mockMessage = ChatMessage.builder()
        .id(messageId)
        .roomId(roomId)
        .memberId(memberId)
        .bookId(bookId)
        .content(content)
        .createdAt(LocalDateTime.now())
        .build();

    when(chatMessageRepository.save(any(ChatMessage.class)))
        .thenReturn(Mono.just(mockMessage));


    Mono<ChatMessage> savedMessage = chatMessageService.saveMessage(memberId,roomId,bookId,content);

    //then
    StepVerifier.create(savedMessage)
        .assertNext(message -> {
          assertEquals(messageId, message.getId());
          assertEquals(roomId, message.getRoomId());
          assertEquals(bookId,message.getBookId());
          assertEquals(content,message.getContent());
        })
        .verifyComplete();
  }


}