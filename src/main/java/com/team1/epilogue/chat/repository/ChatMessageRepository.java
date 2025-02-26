package com.team1.epilogue.chat.repository;

import com.team1.epilogue.chat.entity.ChatMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage,String> {
  //특정 채팅방(roomId)에 속한 메시지를 최신메세지로 부터 조회
  Flux<ChatMessage> findByRoomIdOrderByCreatedAtAsc(String roomId);
}
