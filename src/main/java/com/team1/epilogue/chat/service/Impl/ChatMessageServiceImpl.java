package com.team1.epilogue.chat.service.Impl;

import com.team1.epilogue.chat.entity.ChatMessage;
import com.team1.epilogue.repositories.reactive.ChatMessageRepository;
import com.team1.epilogue.chat.service.ChatMessageService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;

  /**
   * 채팅 메세지를 저장하는 메서드
   * @param memberId 메세지를 보낸 사용자 ID
   * @param roomId 메세지가 속한 채팅방 ID
   * @param content 책 ID (채팅방과 동일)
   * @return 저장된 ChatMessage
   * 메세지 저장.
   */
  public Mono<ChatMessage> saveMessage(Long memberId, String roomId, Long bookId, String content){
    ChatMessage message = ChatMessage.builder()
        .memberId(memberId)
        .bookId(bookId)
        .roomId(roomId)
        .content(content)
        .createdAt(LocalDateTime.now())
        .build();

    return chatMessageRepository.save(message);
  }

  public Flux<ChatMessage> getMessageByRoomId(String roomId) {
    return chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
  }


}
