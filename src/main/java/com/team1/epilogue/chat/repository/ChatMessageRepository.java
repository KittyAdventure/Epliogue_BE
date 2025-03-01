package com.team1.epilogue.chat.repository;

import com.team1.epilogue.chat.entity.ChatMessage;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage,String> {
  List<ChatMessage> findByRoomIdOrderByCreatedAtDesc(String roomId);
}
