package com.team1.epilogue.chat.service;

import com.team1.epilogue.chat.entity.ChatRoom;
import reactor.core.publisher.Mono;

public interface ChatRoomService {
  Mono<ChatRoom> createRoom(Long bookId, String title);
}
