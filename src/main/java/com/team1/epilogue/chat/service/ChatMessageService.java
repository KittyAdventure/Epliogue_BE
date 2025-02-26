package com.team1.epilogue.chat.service;

import com.team1.epilogue.chat.entity.ChatMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatMessageService {
  Mono<ChatMessage> saveMessage(Long memberId, String roomId,  Long bookId,String content);
  Flux<ChatMessage> getMessageByRoomId(String roomId);
}
