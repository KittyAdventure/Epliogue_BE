package com.team1.epilogue.chat.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.book.dto.BookDetailResponse;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.book.service.BookService;
import com.team1.epilogue.chat.dto.ChatMessageDto;
import com.team1.epilogue.chat.dto.ChatRoomDto;
import com.team1.epilogue.chat.entity.ChatMessage;
import com.team1.epilogue.chat.entity.ChatRoom;
import com.team1.epilogue.chat.repository.ChatRoomRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChatRoomServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Mock
  private BookService bookService;

  @Mock
  private BookRepository bookRepository;

  @InjectMocks
  private ChatRoomService chatRoomService;

  private ChatMessageDto chatMessageDto;
  private ChatMessage chatMessage;

  @BeforeEach
  void setUp() {
    chatMessageDto = ChatMessageDto.builder()
        .id("1")
        .roomId("room1")
        .memberId(1L)
        .content("Hello, world!")
        .build();

    chatMessage = new ChatMessage("1", "room1", 1L, "Hello, world!");
  }

  @Test
  void createRoom_Success() {
    when(memberRepository.findById(1L)).thenReturn(Optional.of(new Member()));
    when(bookRepository.findByTitle("Book Title")).thenReturn(Optional.empty());
    when(bookService.getBookDetail(any(), any()))
        .thenReturn(BookDetailResponse.builder().title("Book Title").build());
    when(bookService.insertBookInfo(any())).thenReturn(new Book());
    when(chatRoomRepository.findByTitle(any())).thenReturn(Optional.empty());
    when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(new ChatRoom("1", "Book Title", 0, 1L));

    ChatRoomDto result = chatRoomService.createRoom("Book Title", 1L);

    assertNotNull(result);
    assertEquals("Book Title", result.getTitle());
    assertEquals(0, result.getMemberCnt());
    assertEquals((Long) 1L, result.getCreateId());
  }


  @Test
  void getAllRooms_Success() {
    Pageable pageable = PageRequest.of(0, 10);
    List<ChatRoom> chatRooms = Arrays.asList(new ChatRoom("1", "Room1", 5, 1L));
    Page<ChatRoom> chatRoomPage = new PageImpl<>(chatRooms, pageable, chatRooms.size());

    when(chatRoomRepository.findAll(pageable)).thenReturn(chatRoomPage);

    Page<ChatRoomDto> result = chatRoomService.getAllRooms(0, 10);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals("Room1", result.getContent().get(0).getTitle());
  }
}
