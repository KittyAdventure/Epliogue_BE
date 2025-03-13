package com.team1.epilogue.collection.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.collection.dto.CollectionResponse;
import com.team1.epilogue.collection.entity.CollectionEntity;
import com.team1.epilogue.collection.exception.AlreadyAddedCollectionException;
import com.team1.epilogue.collection.repository.CollectionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

  @Mock
  private BookRepository bookRepository;

  @Mock
  private CollectionRepository collectionRepository;

  @InjectMocks
  private CollectionService collectionService;

  private Member member;
  private Book book;

  @BeforeEach
  void setUp() {
    member = Member.builder()
        .id(1L)
        .loginId("subin")
        .name("이수빈")
        .build();

    book = Book.builder()
        .id("1111111111111")
        .title("테스트1")
        .author("테스트작가")
        .build();
  }

  @Test
  @DisplayName("내가 추가한 컬렉션 책 목록들 불러오기 테스트")
  void getCollection() {
    //given
    List<CollectionEntity> list = new ArrayList<>();
    list.add(
        CollectionEntity.builder()
            .id(1L)
            .member(member)
            .book(book)
            .build()
    );
    list.add(
        CollectionEntity.builder()
            .id(2L)
            .member(member)
            .book(book)
            .build()
    );
    list.add(
        CollectionEntity.builder()
            .id(3L)
            .member(member)
            .book(book)
            .build()
    );

    PageRequest pageRequest = PageRequest.of(0, 9);
    Page<CollectionEntity> page = new PageImpl<>(list, pageRequest, list.size());

    when(collectionRepository.findAllByMember(pageRequest, member))
        .thenReturn(page);

    //when
    CollectionResponse collection = collectionService.getCollection(
        CustomMemberDetails.fromMember(member), 1);

    //then
    assertNotNull(collection);
    assertEquals("1111111111111", collection.getBooks().get(2).getBookId());
  }

  @Test
  @DisplayName("컬렉션 책 추가")
  void addCollection() {
    //given
    when(collectionRepository.existsByMemberAndBook(any(Member.class), any(Book.class))).thenReturn(
        false);
    when(bookRepository.findById("1111111111111")).thenReturn(Optional.of(book));

    //when
    collectionService.addCollection("1111111111111", CustomMemberDetails.fromMember(member));

    //then
    verify(collectionRepository, times(1)).existsByMemberAndBook(any(Member.class),
        any(Book.class));
    verify(collectionRepository, times(1)).save(any(CollectionEntity.class));

  }

  @Test
  @DisplayName("컬렉션 책 추가 - 이미 존재하는 책")
  void addCollection_already_exist() {
    //given
    when(collectionRepository.existsByMemberAndBook(any(Member.class), any(Book.class))).thenReturn(
        true);
    when(bookRepository.findById("1111111111111")).thenReturn(Optional.of(book));

    //when & then
    assertThrows(AlreadyAddedCollectionException.class,
        ()-> collectionService.addCollection("1111111111111", CustomMemberDetails.fromMember(member)));
  }
}