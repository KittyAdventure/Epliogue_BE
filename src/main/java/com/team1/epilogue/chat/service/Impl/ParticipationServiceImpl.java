package com.team1.epilogue.chat.service.Impl;

import com.team1.epilogue.chat.entity.Participation;
import com.team1.epilogue.chat.repository.ChatRoomRepository;
import com.team1.epilogue.chat.repository.ParticipationRepository;
import com.team1.epilogue.chat.service.ParticipationService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {

  private final ParticipationRepository participationRepository;
  private final ChatRoomRepository chatRoomRepository;

  /**
   * @param roomId   채팅방 ID
   * @param memberId 참여하는 사용자 ID
   * @return 참여 정보를 담은 Participation
   */
  @Override
  public Mono<Participation> joinRoom(Long memberId, String roomId) {
    // 참여정보를 생성
    Participation participation = Participation.builder()
        .memberId(memberId)
        .roomId(roomId)
        .joinedAt(LocalDateTime.now())
        .build();
    // 저장 후 반환
    return participationRepository.save(participation);
  }


  // 사용자가 채팅방에서 나감 (참가자 삭제)
  @Override
  public Mono<Void> leaveRoom(Long memberId, String roomId) {
    return participationRepository.findByRoomIdAndMemberId(roomId, memberId)
        .flatMap(participation -> participationRepository.delete(participation))
        .then(checkAndCloseRoom(roomId));
  }

  // 사용자가 0명인 채팅방은 자동으로 방 폐쇄
  private Mono<Void> checkAndCloseRoom(String roomId) {
    return participationRepository.countByRoomId(roomId)
        .flatMap(cnt -> {
          if(cnt == 0) {
            return chatRoomRepository.deleteById(roomId);
          }
          return Mono.empty();
        });
  }



}
