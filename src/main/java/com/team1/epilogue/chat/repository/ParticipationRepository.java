package com.team1.epilogue.chat.repository;

import com.team1.epilogue.chat.entity.Participation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipationRepository extends ReactiveMongoRepository<Participation,String> {
  //특정 사용자의 참여 정보 조회
  Mono<Participation> findByRoomIdAndMemberId(String roomId, Long memberId);

  //특정 채팅방에서 특정 사용자의 참여 정보를 나타내는 repository
  Mono<Long> countByRoomId(String roomId);
}
