package com.team1.epilogue.chat.repository;

import com.team1.epilogue.chat.domain.ChatRoom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ChatRoomRepository extends ReactiveMongoRepository<ChatRoom, Long> {
  // 채팅방을 찾기 위한 메서드
  Mono<ChatRoom> findByBookId(Long bookId);
}
