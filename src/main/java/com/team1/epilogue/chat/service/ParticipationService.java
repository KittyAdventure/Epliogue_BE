package com.team1.epilogue.chat.service;

import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.chat.entity.ChatRoom;
import com.team1.epilogue.chat.entity.Participation;
import com.team1.epilogue.chat.repository.ChatRoomRepository;
import com.team1.epilogue.chat.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipationService {

  private final ParticipationRepository participationRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final MemberRepository memberRepository;


  /**
   * 채팅방 ID
   * 참여하는 사용자 ID
   * @return 참여 정보를 담은 Participation
   */
  public boolean joinRoom(String roomId, Long memberId){
    memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException());

    ChatRoom chatRoom = chatRoomRepository.findById(roomId)
        .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

    //최대인원 초과
    if(!chatRoom.participantsLimit(memberId)) {
      return false;
    }
    //참여 정보를 저장
    if(!participationRepository.existsByRoomIdAndMemberId(roomId,memberId)){
      Participation participation = Participation.builder()
          .roomId(roomId)
          .memberId(memberId)
          .build();

      participationRepository.save(participation);
    }

    chatRoomRepository.save(chatRoom);
    return true;
  }


  /**
   * 채팅방 나가기(0명이 되면 삭제)
   */
  public void leaveRoom(String roomId, Long memberId){
    memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException());

    //참여기록 삭제
    participationRepository.findByRoomId(roomId).stream()
        .filter(p -> p.getMemberId().equals(memberId))
        .findFirst()
        .ifPresent(participationRepository::delete);

    // 남은 인원 확인 후 삭제
    if (participationRepository.findByRoomId(roomId).isEmpty()) {
      chatRoomRepository.deleteById(roomId);
    }
  }



}
