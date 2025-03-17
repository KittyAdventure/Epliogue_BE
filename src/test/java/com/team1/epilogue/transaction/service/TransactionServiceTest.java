package com.team1.epilogue.transaction.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.transaction.domain.TransactionDetail;
import com.team1.epilogue.transaction.dto.BoughtItemList;
import com.team1.epilogue.transaction.dto.ItemList;
import com.team1.epilogue.transaction.dto.TransactionHistoryRequest;
import com.team1.epilogue.transaction.dto.TransactionHistoryResponse;
import com.team1.epilogue.transaction.entity.Item;
import com.team1.epilogue.transaction.entity.ItemBuyHistory;
import com.team1.epilogue.transaction.entity.NotEnoughPointException;
import com.team1.epilogue.transaction.entity.Transaction;
import com.team1.epilogue.transaction.exception.AlreadyBoughtItemException;
import com.team1.epilogue.transaction.repository.ItemBuyHistoryRepository;
import com.team1.epilogue.transaction.repository.ItemRepository;
import com.team1.epilogue.transaction.repository.TransactionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
class TransactionServiceTest {

  @Mock
  private TransactionRepository transactionRepository;
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private ItemRepository itemRepository;
  @Mock
  private ItemBuyHistoryRepository buyHistoryRepository;

  @InjectMocks
  private TransactionService transactionService;

  private Member member;

  @BeforeEach
  void setUp() {
    member = Member.builder()
        .id(1L)
        .loginId("test")
        .name("수빈")
        .point(1000)
        .build();
  }

  @Test
  @DisplayName("회원 포인트 변경 테스트")
  void updateBalance() {
    //given
    when(memberRepository.findByLoginIdWithLock("test")).thenReturn(Optional.of(member));

    //when
    transactionService.updateBalance("test", 100, TransactionDetail.ITEM, null);

    //then
    verify(memberRepository, times(1)).save(member);
    verify(transactionRepository, times(1)).save(any(Transaction.class));
  }

  @Test
  @DisplayName("회원 거래내역 조회 기능")
  void getTransactionHistory() {
    //given
    int page = 0;
    when(memberRepository.findByLoginId("test")).thenReturn(Optional.of(member));
    List<Transaction> list = new ArrayList<>();
    list.add(
        Transaction.builder()
            .id(1L)
            .member(member)
            .dateTime(LocalDateTime.now())
            .amount(100)
            .build()
    );
    list.add(
        Transaction.builder()
            .id(2L)
            .member(member)
            .dateTime(LocalDateTime.now())
            .amount(1000)
            .build()
    );
    list.add(
        Transaction.builder()
            .id(3L)
            .member(member)
            .dateTime(LocalDateTime.now())
            .amount(10000)
            .build()
    );
    PageRequest request = PageRequest.of(page, 9);
    Page<Transaction> response = new PageImpl<>(list, request, list.size());

    when(transactionRepository.findByDateTimeBetweenAndMemberId(any(LocalDateTime.class), any(
        LocalDateTime.class), eq(1L), eq(request))).thenReturn(response);

    TransactionHistoryRequest build = TransactionHistoryRequest.builder()
        .startDate(LocalDate.now())
        .endDate(LocalDate.now())
        .page(1)
        .limit(9)
        .build();

    //when
    TransactionHistoryResponse result = transactionService.getTransactionHistory(
        CustomMemberDetails.fromMember(member), build);

    //then
    assertEquals(1, result.getTotal());
    assertEquals(3, result.getData().size());
  }

  @Test
  @DisplayName("아이템 구매기능 테스트")
  void buyItem() {
    //given
    Item item = Item.builder()
        .id(1L)
        .price(100)
        .name("테스트")
        .build();

    when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
    when(memberRepository.findByLoginIdWithLock("test")).thenReturn(Optional.of(member));
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(buyHistoryRepository.existsByItemIdAndMemberId(1L, 1L)).thenReturn(false);

    //when
    boolean buyItem = transactionService.buyItem(CustomMemberDetails.fromMember(member), 1L);

    //then
    assertTrue(buyItem);
    verify(buyHistoryRepository, times(1)).save(any(ItemBuyHistory.class));

  }

  @Test
  @DisplayName("아이템 구매기능 테스트 - 이미 구매한 아이템")
  void buyItem_already_bought() {
    //given
    Item item = Item.builder()
        .id(1L)
        .price(100)
        .name("테스트")
        .build();

    when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(buyHistoryRepository.existsByItemIdAndMemberId(1L, 1L)).thenReturn(true);

    //when & then
    assertThrows(AlreadyBoughtItemException.class,
        () -> transactionService.buyItem(CustomMemberDetails.fromMember(member), 1L));
  }

  @Test
  @DisplayName("아이템 구매기능 테스트 - 포인트 부족")
  void buyItem_not_enough_point() {
    //given
    Item item = Item.builder()
        .id(1L)
        .price(100000)
        .name("테스트")
        .build();

    when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

    //when & then
    assertThrows(NotEnoughPointException.class,
        () -> transactionService.buyItem(CustomMemberDetails.fromMember(member), 1L));
  }

  @Test
  @DisplayName("구매한 아이템 리스트 불러오기")
  void getBoughtItemList() {
    //given
    when(memberRepository.findByLoginId("test")).thenReturn(Optional.of(member));
    Item item = Item.builder()
        .id(1L)
        .name("테스트")
        .price(100)
        .build();

    PageRequest request = PageRequest.of(0, 10);

    List<ItemBuyHistory> histories = new ArrayList<>();
    histories.add(
        ItemBuyHistory.builder()
            .id(1L)
            .item(item)
            .createdAt(LocalDateTime.now())
            .build()
    );
    histories.add(
        ItemBuyHistory.builder()
            .id(2L)
            .item(item)
            .createdAt(LocalDateTime.now())
            .build()
    );
    histories.add(
        ItemBuyHistory.builder()
            .id(3L)
            .item(item)
            .createdAt(LocalDateTime.now())
            .build()
    );

    PageImpl<ItemBuyHistory> response = new PageImpl<>(histories, request, histories.size());
    when(buyHistoryRepository.findAllByMemberId(1L, request)).thenReturn(response);

    //when
    BoughtItemList result = transactionService.getBoughtItemList("test", 1);

    //then
    assertEquals(1, result.getTotalPages());
    assertEquals(3, result.getItems().size());
  }

  @Test
  @DisplayName("상점에서 아이템 리스트 불러오기")
  void getItemList() {
    //given
    when(memberRepository.findByLoginId("test")).thenReturn(Optional.of(member));
    PageRequest request = PageRequest.of(0, 6);
    Item item = Item.builder()
        .id(1L)
        .name("테스트")
        .price(100)
        .build();

    List<Item> items = new ArrayList<>();
    items.add(item);
    items.add(item);
    items.add(item);

    Page<Item> itemList = new PageImpl<>(items, request, items.size());

    when(buyHistoryRepository.findAllItemIdsByMemberId(1L)).thenReturn(new ArrayList<>());
    when(itemRepository.findAll(request)).thenReturn(itemList);

    //when
    ItemList response = transactionService.getItemList("test", 1);

    //then
    assertEquals(1, response.getTotalPages());
    assertEquals(3, response.getItems().size());

  }
}