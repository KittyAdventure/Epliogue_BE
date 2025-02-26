package com.team1.epilogue.chat.repository;

import com.team1.epilogue.chat.entity.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatRoomRepository extends ReactiveMongoRepository<ChatRoom, String> {
  Flux<ChatRoom> findAllBy(Pageable pageable);
}
