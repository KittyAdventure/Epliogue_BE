package com.team1.epilogue.chat.service;

import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.chat.dto.ChatMessageDto;
import com.team1.epilogue.chat.entity.ChatMessage;
import com.team1.epilogue.chat.repository.ChatMessageRepository;
import com.team1.epilogue.chat.repository.ChatRoomRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final MemberRepository memberRepository;
  private final ChatRoomRepository chatRoomRepository;
  /**
   * 채팅 메세지를 저장하는 메서드
   * memberId 메세지를 보낸 사용자 ID
   * roomId 메세지가 속한 채팅방 ID
   * content 책 ID (채팅방과 동일)
   *  저장된 ChatMessage
   */

  public ChatMessageDto saveMessage(ChatMessageDto chatMessageDto) {
    //사용자 인증
    memberRepository.findById(chatMessageDto.getMemberId())
        .orElseThrow(() -> new MemberNotFoundException());

    //채팅방 존재 여부
    chatRoomRepository.findById(chatMessageDto.getRoomId())
        .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

    //메세지 저장
    ChatMessage chatMessage = chatMessageDto.fromEntity();
    ChatMessage saveMessage = chatMessageRepository.save(chatMessage);

    return ChatMessageDto.builder()
        .roomId(saveMessage.getRoomId())
        .memberId(saveMessage.getMemberId())
        .content(saveMessage.getContent())
        .createdAt(saveMessage.getCreatedAt())
        .build();
  }


  /**
   * roomId
   * @return 메세지를 최신순으로 조회
   */
  public List<ChatMessageDto> getMessagesByRoom(String roomId) {
    return chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId).stream()
        .map(chatMessage -> ChatMessageDto.builder()
            .roomId(chatMessage.getRoomId())
            .memberId(chatMessage.getMemberId())
            .content(chatMessage.getContent())
            .createdAt(chatMessage.getCreatedAt())
            .build())
        .collect(Collectors.toList());
  }




}
