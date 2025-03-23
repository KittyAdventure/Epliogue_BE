package com.team1.epilogue.chat.service;

import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.book.dto.BookDetailRequest;
import com.team1.epilogue.book.dto.BookDetailResponse;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.book.service.BookService;
import com.team1.epilogue.chat.dto.ChatRoomDto;
import com.team1.epilogue.chat.entity.ChatRoom;
import com.team1.epilogue.chat.repository.ChatRoomRepository;
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
  private final BookService bookService;
  private final BookRepository bookRepository;

  /**
   * 채팅방을 생성 채팅방 생성시 책 이름과 동일함
   */
  public ChatRoomDto createRoom(String title, Long memberId) {
    //사용자가 유효한지 확인(회원 존재 체크 여부)
    memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException());

    Book book = bookRepository.findByTitle(title)
        .orElseGet(() -> {
          BookDetailRequest request = BookDetailRequest.builder()
              .type("title")
              .query(title)
              .build();

          BookDetailResponse bookDetailResponse = bookService.getBookDetail(request.getQuery(),
              request.getType());

          return bookService.insertBookInfo(bookDetailResponse);
        });

    String chatRoomTitle = book.getTitle();

    ChatRoom chatRoom = chatRoomRepository.findByTitle(chatRoomTitle)
        .orElseGet(() -> chatRoomRepository.save(ChatRoom.builder()
            .title(chatRoomTitle)
            .memberCnt(0)
            .createId(memberId)
            .build()
        ));

    return ChatRoomDto.builder()
        .id(chatRoom.getId())
        .title(chatRoom.getTitle())
        .memberCnt(chatRoom.getMemberCnt())
        .createId(chatRoom.getCreateId())
        .build();

  }

  /**
   * 전체 채팅방 조회
   */
  public Page<ChatRoomDto> getAllRooms(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);// 페이지 번호와 크기를 결정
    Page<ChatRoom> chatRoomsPage = chatRoomRepository.findAll(pageable); // 페이징 처리된 데이터 조회

    return chatRoomsPage.map(chatRoom -> ChatRoomDto.builder()
        .id(chatRoom.getId())
        .title(chatRoom.getTitle())
        .memberCnt(chatRoom.getMemberCnt())
        .createId(chatRoom.getCreateId())
        .build());
  }


}
