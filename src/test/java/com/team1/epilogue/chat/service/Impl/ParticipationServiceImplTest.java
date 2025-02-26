package com.team1.epilogue.chat.service.Impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team1.epilogue.chat.entity.Participation;
import com.team1.epilogue.repositories.reactive.ChatRoomRepository;
import com.team1.epilogue.repositories.reactive.ParticipationRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


class ParticipationServiceImplTest {

  @Mock
  private ParticipationRepository participationRepository;

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @InjectMocks
  private ParticipationServiceImpl participationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("채팅방 참여 테스트")
  void chatting_join() {
    // Given
    Long roomId = 123L;  // roomId를 Long 타입으로 변경
    Long memberId = 12L;

    Participation participation = Participation.builder()
        .roomId(String.valueOf(roomId)) // `Participation`에서는 String 사용
        .memberId(memberId)
        .joinedAt(LocalDateTime.now())
        .build();

    // 저장 시 Mock 객체 반환
    when(participationRepository.save(any(Participation.class))).thenReturn(Mono.just(participation));

    // When
    Mono<Participation> result = participationService.joinRoom(memberId, String.valueOf(roomId));

    // Then
    StepVerifier.create(result)
        .expectNext(participation)
        .verifyComplete();

    verify(participationRepository, times(1)).save(any(Participation.class));
  }

}