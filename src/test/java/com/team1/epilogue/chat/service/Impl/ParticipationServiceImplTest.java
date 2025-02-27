package com.team1.epilogue.chat.service.Impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team1.epilogue.chat.entity.Participation;
import com.team1.epilogue.chat.repository.ParticipationRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ParticipationServiceImplTest {

  @Mock
  private ParticipationRepository participationRepository;


  @InjectMocks
  private ParticipationServiceImpl participationService;

  private static final Logger logger = LoggerFactory.getLogger(ParticipationServiceImplTest.class);

  @Test
  @DisplayName("채팅방 참여 테스트")
  void chatting_join() {
    // Given
    String roomId = "123"; // 방번호
    Long memberId = 12L; // memberId

    Participation participation = Participation.builder()
        .roomId(roomId)
        .memberId(memberId)
        .joinedAt(LocalDateTime.now())
        .build();

    // Mock 객체가 Save() 호출 시 participation을 반환하도록 설정
    when(participationRepository.save(any(Participation.class))).thenReturn(Mono.just(participation));

    // When : 참여 요청
    Mono<Participation> result = participationService.joinRoom(memberId, roomId);

    // Then : 정상적으로 참여 정보가 반환되는지 검증
    StepVerifier.create(result)
        .expectSubscription() // 구독이 정상적으로 이루어져있는지 확인
        .expectNextMatches(savedParticipation  -> {
          logger.info("저장된 Participation: " + savedParticipation );
          return savedParticipation .getRoomId().equals(roomId) && savedParticipation .getMemberId().equals(memberId);
        })
//        .expectNext(participation) // participation 객체가 반환되었는지 확인
        .verifyComplete(); // 정상 종료 확인

    // save()가 1번 호출되었는지 검증
    verify(participationRepository, times(1)).save(any(Participation.class));
  }

}