package com.team1.epilogue.transaction.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.transaction.domain.TransactionDetail;
import com.team1.epilogue.transaction.dto.BoughtItemDetail;
import com.team1.epilogue.transaction.dto.BoughtItemList;
import com.team1.epilogue.transaction.dto.ItemList;
import com.team1.epilogue.transaction.dto.ItemListDetail;
import com.team1.epilogue.transaction.dto.TransactionHistoryDto;
import com.team1.epilogue.transaction.dto.TransactionHistoryRequest;
import com.team1.epilogue.transaction.dto.TransactionHistoryResponse;
import com.team1.epilogue.transaction.entity.Item;
import com.team1.epilogue.transaction.entity.ItemBuyHistory;
import com.team1.epilogue.transaction.entity.NotEnoughPointException;
import com.team1.epilogue.transaction.entity.Transaction;
import com.team1.epilogue.transaction.exception.AlreadyBoughtItemException;
import com.team1.epilogue.transaction.exception.ItemNotFoundException;
import com.team1.epilogue.transaction.repository.ItemBuyHistoryRepository;
import com.team1.epilogue.transaction.repository.ItemRepository;
import com.team1.epilogue.transaction.repository.TransactionRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final MemberRepository memberRepository;
  private final ItemRepository itemRepository;
  private final ItemBuyHistoryRepository buyHistoryRepository;

  /**
   * 포인트 충전 / 획득 / 사용 을 담당하는 메서드 입니다. 양수 = 충전,획득 / 음수 = 사용
   *
   * @param memberId 사용자 ID
   * @param amount   포인트 변동량
   * @param detail   사용처 , 충전 , 획득처 기록하는 Enum
   * @param tid      TransactionDetail 이 KAKAO 일때만 기록되는 카카오페이 취소를 위한 TID (다른때엔 NULL)
   */
  @Transactional
  public void updateBalance(String memberId, int amount, TransactionDetail detail, String tid) {
    // 사용자 정보 가져오기 존재하지않다면 Exception
    Member member = memberRepository.findByLoginIdWithLock(memberId)
        .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 사용자입니다."));

    // 거래정보 저장을 위한 Transaction 객체 생성
    Transaction data = Transaction.builder()
        .member(member)
        .amount(amount)
        .afterBalance(member.getPoint() + amount)
        .detail(detail)
        .build();

    if (detail == TransactionDetail.KAKAOPAY) {
      data = data.toBuilder().tid(tid).build();
    }

    // member Point 변경
    member.setPoint(member.getPoint() + amount);

    transactionRepository.save(data); // DB 에 거래내역 저장
    memberRepository.save(member); // 사용자의 포인트 보유량 변경
  }

  /**
   * 회원의 거래내역을 조회하는 메서드입니다.
   *
   * @param dto 시작할 날짜와 끝나는 날짜 , 페이지 데이터 갯수제한, 페이지 번호를 담은 DTO
   * @return TransactionHistoryResponse 객체 return
   */
  public TransactionHistoryResponse getTransactionHistory(TransactionHistoryRequest dto) {
    // 시작 날짜 설정
    LocalDateTime start = dto.getStartDate().atStartOfDay();
    // 끝나는 날짜 설정
    LocalDateTime end = dto.getEndDate().atTime(23, 59, 59);
    // 페이징 설정 -> 프론트에서 넘어온 페이지 번호와, 한 페이지 데이터 갯수 설정
    Pageable pageable = PageRequest.of(dto.getPage(), dto.getLimit());

    // Repository 에서 거래내역 가져오기
    Page<Transaction> response = transactionRepository
        .findByDateTimeBetween(start, end, pageable);

    // 데이터 API 명세서에 맞게 가공해서 return
    return TransactionHistoryResponse.builder()
        .total(response.getNumberOfElements())
        .limit(response.getSize())
        .page(dto.getPage())
        // Page 객체 내부의 Transaction Entity -> DTO 로 변환
        .data(response.getContent().stream().map(data -> TransactionHistoryDto.toDto(data))
            .collect(Collectors.toList()))
        .build();
  }

  @Transactional
  public boolean buyItem(CustomMemberDetails customMemberDetails, Long itemId) {
    Member member = memberRepository.findById(customMemberDetails.getId()).orElseThrow(
        () -> new MemberNotFoundException("존재하지 않는 회원입니다.")
    );

    Item item = itemRepository.findById(itemId).orElseThrow(
        () -> new ItemNotFoundException("존재하지 않는 아이템입니다.")
    );

    if (member.getPoint() < item.getPrice()) {
      throw new NotEnoughPointException("포인트가 충분하지 않습니다");
    }

    if (buyHistoryRepository.existsByItemIdAndMemberId(item.getId(), member.getId())) {
      throw new AlreadyBoughtItemException("이미 구매한 아이템 입니다.");
    }

    updateBalance(member.getLoginId(), item.getPrice() * -1, TransactionDetail.ITEM, null);

    ItemBuyHistory history = ItemBuyHistory.builder()
        .item(item)
        .member(member)
        .build();

    buyHistoryRepository.save(history);

    return true;
  }

  public BoughtItemList getBoughtItemList(String memberId, int page) {
    Member member = memberRepository.findByLoginId(memberId).orElseThrow(
        () -> new MemberNotFoundException("존재하지 않는 회원입니다.")
    );
    PageRequest request = PageRequest.of(page, 10);

    Page<ItemBuyHistory> allHistories = buyHistoryRepository.findAllByMemberId(member.getId(),
        request);

    List<BoughtItemDetail> list = new ArrayList<>();

    allHistories.getContent().stream().forEach(
        data -> {
          list.add(
              BoughtItemDetail.builder()
                  .itemId(data.getItem().getId())
                  .name(data.getItem().getName())
                  .price(data.getItem().getPrice())
                  .buyDate(data.getCreatedAt().toLocalDate())
                  .build()
          );
        }
    );

    return BoughtItemList.builder()
        .page(page)
        .totalPages(allHistories.getTotalPages())
        .items(list)
        .build();
  }

  public ItemList getItemList(String memberId, int page) {
    Member member = memberRepository.findByLoginId(memberId).orElseThrow(
        () -> new MemberNotFoundException("존재하지 않는 회원입니다.")
    );
    PageRequest request = PageRequest.of(page, 6);

    Page<Item> allItems = itemRepository.findAll(request); //6개씩 페이징 처리해서 아이템 리스트를 불러옴.

    // 해당 회원이 구매한 아이템들의 PK 값 List 를 불러옴
    List<Long> allItemsHistories = buyHistoryRepository.findAllItemIdsByMemberId(member.getId());

    List<ItemListDetail> list = new ArrayList<>();
    allItems.getContent().stream().forEach(
        data -> {
          list.add(
              ItemListDetail.builder()
                  .id(data.getId())
                  .name(data.getName())
                  .price(data.getPrice())
                  .buy(allItemsHistories.contains(data.getId())) // allItemsHistories 에 포함되어있으면 구매한 아이템이다.
                  .build());
        }
    );

    return ItemList.builder()
        .page(page)
        .totalPages(allItems.getTotalPages())
        .items(list)
        .build();
  }
}
