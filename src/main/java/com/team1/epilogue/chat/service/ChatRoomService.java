package com.team1.epilogue.chat.service;

import com.team1.epilogue.chat.entity.ChatRoom;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatRoomService {
  Mono<ChatRoom> createRoom(String title);
  Flux<ChatRoom> getChatRooms(int page, int limit);
  Mono<Void> deleteRoom(String id);
}
