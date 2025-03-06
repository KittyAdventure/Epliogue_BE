package com.team1.epilogue.chat.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.chat.entity.ChatRoom;
import com.team1.epilogue.chat.entity.Participation;
import com.team1.epilogue.chat.repository.ChatRoomRepository;
import com.team1.epilogue.chat.repository.ParticipationRepository;
import com.team1.epilogue.config.MongoConfig;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;



@Slf4j
@ExtendWith(MockitoExtension.class)
@Import(MongoConfig.class) // Auditing 활성화
class ParticipationServiceTest {

  ObjectMapper objectMapper = new ObjectMapper();

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private ParticipationRepository participationRepository;



  @InjectMocks
  private ParticipationService participationService;

  @Test
  @DisplayName("채팅방 참여 테스트")
  void joinRoom_success() throws JsonProcessingException {
    //given
    String roomId = "room1";
    Long memberId = 1L;

    Member member = new Member(); //가짜 회원 객체 생성
    ReflectionTestUtils.setField(member, "id", memberId);

    ChatRoom chatRoom = ChatRoom.builder()
        .id(roomId)
        .title("헨젤과 그레텔")
        .participants(new HashSet<>())
        .build();

    Participation participation = Participation.builder()
        .roomId(roomId)
        .memberId(memberId)
        .build();
    String savePartition = objectMapper.writeValueAsString(participation);
    log.info("Before Save: {}" + savePartition);

    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(chatRoom));
    when(participationRepository.existsByRoomIdAndMemberId(roomId, memberId)).thenReturn(false);
    when(participationRepository.save(any(Participation.class))).thenReturn(participation);
    when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);

    //when
    boolean result = participationService.joinRoom(roomId,memberId);

    String jsonParticipation = objectMapper.writeValueAsString(participation);
    //then - 결과물
    assertTrue(result);

    log.info("채팅방 참가:" + jsonParticipation);
  }


  @Test
  @DisplayName("채팅방 나가기")
  void leaveRoom_success() {
    //given
    String roomId = "room1";
    Long memberId = 1L;

    Member member = new Member();
    ReflectionTestUtils.setField(member, "id", memberId);

    Participation participation = Participation.builder()
        .roomId(roomId)
        .memberId(memberId)
        .build();

    List<Participation> participations = new ArrayList<>();
    participations.add(participation);

    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    when(participationRepository.findByRoomId(roomId)).thenReturn(participations);

    //when
    participationService.leaveRoom(roomId,memberId);


    //then
    verify(participationRepository).delete(participation);
    verify(chatRoomRepository, never()).deleteById(anyString());
  }

}