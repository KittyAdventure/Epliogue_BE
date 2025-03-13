package com.team1.epilogue.chat.repository;

import com.team1.epilogue.chat.entity.ChatRoom;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findByTitle(String title);
}
