package com.team1.epilogue.chat.service.Impl;

import com.team1.epilogue.chat.domain.Participation;
import com.team1.epilogue.chat.repository.ParticipationRepository;
import com.team1.epilogue.chat.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {

  private final ParticipationRepository participationRepository;

  /**
   * @param roomId   채팅방 ID
   * @param memberId 참여하는 사용자 ID
   * @return 참여 정보를 담은 Participation Mono
   */
  @Override
  public Mono<Participation> joinRoom(Long memberId, String roomId) {
    Participation participation = Participation.builder()
        .memberId(memberId)
        .roomId(roomId)
        .build();

    return participationRepository.save(participation);
  }

  /**
   * 채팅방
   */
  @Override
  public Flux<Participation> getParticipants(String roomId) {
    return participationRepository.findByRoomId(roomId);
  }

  /**
   *
   * @param memberId
   * 채팅방 멤버 조회
   */
  @Override
  public Flux<Participation> getUserRooms(Long memberId) {
    return participationRepository.findByMemberId(memberId);
  }
}
