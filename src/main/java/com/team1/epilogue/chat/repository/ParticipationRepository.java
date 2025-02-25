package com.team1.epilogue.chat.repository;

import com.team1.epilogue.chat.domain.Participation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ParticipationRepository extends ReactiveMongoRepository<Participation,Long> {
  //특정 채팅방(roomId)에 참여한 모든 사용자 조회
  Flux<Participation> findByRoomId(String roomId);

  //특정 채팅방에서 특정 사용자의 참여 정보를 나타내는 repository
  Flux<Participation> findByMemberId(Long MemberId);
}
