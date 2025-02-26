package com.team1.epilogue.chat.repository;

import com.team1.epilogue.chat.entity.Participation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ParticipationRepository extends ReactiveMongoRepository<Participation,String> {
  //특정 채팅방(roomId)에 참여한 모든 사용자 조회
  Flux<Participation> findByRoomId(String roomId);

  //특정 채팅방에서 특정 사용자의 참여 정보를 나타내는 repository
  Flux<Participation> findByMemberId(Long MemberId);
}
