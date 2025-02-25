package com.team1.epilogue.chat.service;

import com.team1.epilogue.chat.entity.Participation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ParticipationService {

  Mono<Participation> joinRoom(Long memberId, String roomId);
  Flux<Participation> getParticipants(String roomId);
  Flux<Participation> getUserRooms(Long memberId);
}
