package com.team1.epilogue.chat.repository;

import com.team1.epilogue.chat.entity.Participation;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipationRepository extends MongoRepository<Participation,String> {
  boolean existsByRoomIdAndMemberId(String roomId, Long memberId);
  List<Participation> findByRoomId(String roomId);
}
