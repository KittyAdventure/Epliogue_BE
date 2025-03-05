package com.team1.epilogue.chat.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.chat.dto.ChatRoomDto;
import com.team1.epilogue.chat.entity.ChatRoom;
import com.team1.epilogue.chat.repository.ChatRoomRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {


  @InjectMocks
  private ChatRoomService chatRoomService;
  @Mock
  private ChatRoomRepository chatRoomRepository;
  @Mock
  private MemberRepository memberRepository;

  @Test
  @DisplayName("채팅방 생성")
  void createRoom_success() {
    //given
    String title= "책 제목";
    Long memberId = 1L;

    Member member = new Member(); // Mock 멤버
    ChatRoom chatRoom = ChatRoom.builder()
        .title(title)
        .participants(new HashSet<>())
        .build();

    ChatRoom savedChatRoom = ChatRoom.builder()
        .id("room1")
        .title(title)
        .participants(new HashSet<>(Set.of(memberId)))
        .build();

    // Mock 설정
    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
//    when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);
    when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(savedChatRoom);

    //when
    ChatRoomDto result = chatRoomService.createRoom(title,memberId);

    // then
    assertNotNull(result);
    assertEquals("room1",result.getId());
    assertEquals(title, result.getTitle());
    assertTrue(result.getParticipants().contains(memberId));

    verify(memberRepository, times(1)).findById(memberId);
    verify(chatRoomRepository, times(2)).save(any(ChatRoom.class)); // 최초 저장 + 참여자 추가 후 저장

  }


  @Test
  @DisplayName("전체 채팅방 조회 테스트")
  void getAllRooms() {
    //given
    ChatRoom chatRoom1 = ChatRoom.builder()
        .title("헨젤과 그레텔!")
        .participants(new HashSet<>())
        .build();

    ChatRoom chatRoom2 = ChatRoom.builder()
        .title("콩쥐 팥쥐")
        .participants(new HashSet<>())
        .build();

    List<ChatRoom> chatRooms = Arrays.asList(chatRoom1,chatRoom2);
    Pageable pageable = PageRequest.of(0,2);
    Page<ChatRoom> chatRoomPage = new PageImpl<>(chatRooms, pageable,chatRooms.size());

    when(chatRoomRepository.findAll(any(Pageable.class))).thenReturn(chatRoomPage);

    //when
    List<ChatRoomDto> result = chatRoomService.getAllRooms(0,2);

    //then 결과 검증
    assertNotNull(result);
    assertEquals(2,result.size());
    assertEquals("헨젤과 그레텔!", result.get(0).getTitle());
    assertEquals("콩쥐 팥쥐", result.get(1).getTitle());

  }
}