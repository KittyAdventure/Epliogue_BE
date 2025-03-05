package com.team1.epilogue.chat.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.chat.dto.ChatRoomDto;
import com.team1.epilogue.chat.entity.ChatRoom;
import com.team1.epilogue.chat.repository.ChatRoomRepository;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final MemberRepository memberRepository;

  /**
   * 채팅방을 생성 채팅방 생성시 책 이름과 동일함
   */
  public ChatRoomDto createRoom(String title, Long memberId) {
    //사용자가 유효한지 확인(회원 존재 체크 여부)
    memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException());

    // 채팅방 생성
    ChatRoom chatRoom = ChatRoom.builder()
        .title(title)
        .participants(new HashSet<>())
        .build();

    //채팅방 저장
    ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
    log.info("채팅방 저장 : " + savedRoom);

    // 채팅방 생성 후, 참여자에 생성자 추가
    chatRoom.participantsLimit(memberId); // 참여자 추가
//    chatRoomRepository.save(chatRoom); // 참여자 추가 후 저장

    log.info("참여자 추가: " + chatRoom.participantsLimit(memberId));
    log.info("참여자 추가 후 저장 : " + chatRoomRepository.save(chatRoom));
    return ChatRoomDto.fromEntity(savedRoom);
  }

  /**
   * 전체 채팅방 조회
   */
  public List<ChatRoomDto> getAllRooms(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);// 페이지 번호와 크기를 결정
    Page<ChatRoom> chatRoomsPage = chatRoomRepository.findAll(pageable); // 페이징 처리된 데이터 조회

    return chatRoomsPage.getContent().stream() // 실제 데이터 목록 가져오기
        .map(ChatRoomDto::fromEntity)
        .collect(Collectors.toList());
  }


}
