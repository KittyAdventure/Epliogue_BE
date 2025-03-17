package com.team1.epilogue.transaction.controller;

import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.comment.dto.MessageResponse;
import com.team1.epilogue.transaction.domain.TransactionDetail;
import com.team1.epilogue.transaction.dto.BoughtItemList;
import com.team1.epilogue.transaction.dto.ItemList;
import com.team1.epilogue.transaction.dto.KakaoPayApproveResponse;
import com.team1.epilogue.transaction.dto.KakaoPayRedirectUrlDto;
import com.team1.epilogue.transaction.dto.TransactionHistoryRequest;
import com.team1.epilogue.transaction.dto.TransactionHistoryResponse;
import com.team1.epilogue.transaction.service.KakaoPayService;
import com.team1.epilogue.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
/*
 * 카카오 페이 결제 & 기타 포인트 관련 컨트롤러입니다.
 */
public class TransactionController {

  private final TransactionService transactionService;
  private final KakaoPayService kakaoPayService;

  /**
   * 카카오페이 결제 준비를 요청하는 기능입니다.
   *
   * @param authentication 사용자 정보를 얻기위한 authentication 객체
   * @param amount         충전 금액
   * @return 사용자에게 보여지는 Redirect Url 을 return
   */
  @PostMapping("/api/kp/prepare")
  public ResponseEntity<KakaoPayRedirectUrlDto> kakaoPayPrepare(
      Authentication authentication,
      @RequestParam int amount) {
    CustomMemberDetails details = (CustomMemberDetails) authentication.getPrincipal();
    String memberId = details.getUsername();
    String redirectUrl = kakaoPayService.prepareCharge(memberId, amount).getNextRedirectPCUrl();

    // 프론트 측으로 KakaoPay 서버에서온 Url 주소 return
    return ResponseEntity.ok(KakaoPayRedirectUrlDto.builder()
        .redirectUrl(redirectUrl)
        .build());
  }

  /**
   * 카카오페이 결제가 승인되었을때 카카오쪽에서 redirect 되는 URL
   *
   * @param memberId 사용자 ID
   * @param pgToken  카카오 측에서 발급해준 pgToken
   * @return 결제 성공시 200 응답
   */
  @GetMapping("/api/kp/success")
  public ResponseEntity<MessageResponse> kakaoPaySuccess(
      @RequestParam int amount,
      @RequestParam String memberId,
      @RequestParam("pg_token") String pgToken) {
    KakaoPayApproveResponse response = kakaoPayService.approveCharge(
        memberId,
        pgToken); // 준비 -> 승인 2단계에 걸쳐 카카오페이 결제가 진행된다.
    // 이 메서드가 정상적으로 처리되었다면 카카오쪽에서도 승인된것.(결제 성공)

    transactionService.updateBalance(memberId, amount, TransactionDetail.KAKAOPAY,
        response.getTid());
    // DB 에 저장 , 회원 포인트 정보 변경

    MessageResponse responseToFront = MessageResponse.builder()
        .message("정상적으로 처리되었습니다.").build();

    return ResponseEntity.ok(responseToFront);
  }

  /**
   * 카카오페이 쪽에서 결제 실패했을때 redirect 되는 URL
   *
   * @return 400 응답
   */
  @GetMapping("/api/kp/fail")
  public ResponseEntity<MessageResponse> kakaoPayFail() {
    MessageResponse response = MessageResponse.builder()
        .message("결제가 실패하였습니다.").build();
    return ResponseEntity.badRequest().body(response);
  }

  /**
   * 카카오페이 쪽에서 결제 취소됐을때 redirect 되는 URL
   *
   * @return 400 응답
   */
  @GetMapping("/api/kp/cancel")
  public ResponseEntity<MessageResponse> kakaoPayCancel() {
    MessageResponse response = MessageResponse.builder()
        .message("결제가 취소되었습니다.").build();
    return ResponseEntity.badRequest().body(response);
  }

  /**
   * 회원의 거래내역을 조회하는 메서드입니다.
   *
   * @param dto 시작할 날짜와 끝나는 날짜 , 페이지 데이터 갯수제한, 페이지 번호를 담은 DTO
   * @return TransactionHistoryResponse 객체 return
   */
  @GetMapping("/api/transaction")
  public ResponseEntity<TransactionHistoryResponse> getTransactionHistory(
      @RequestBody TransactionHistoryRequest dto) {
    TransactionHistoryResponse response = transactionService.getTransactionHistory(dto);
    return ResponseEntity.ok(response);
  }

  /**
   * 카카오페이 환불을 위한 메서드입니다.
   *
   * @param authentication 사용자 인증정보를 담은 authentication 객체
   * @param transactionId  DB 내부에 존재하는 거래 정보의 PK
   * @return 성공시 200 응답
   */
  @PostMapping("/api/kp/{transactionId}/refund")
  public ResponseEntity<MessageResponse> kakaoPayRefund(Authentication authentication,
      @PathVariable Long transactionId) {
    String memberId = authentication.getName();
    kakaoPayService.kakaoPayRefund(memberId, transactionId);
    MessageResponse response = MessageResponse.builder()
        .message("취소 요청이 정상적으로 처리되었습니다.").build();
    return ResponseEntity.ok().body(response);
  }

  /**
   * 아이템 구매하는 메서드
   */
  @PostMapping("/api/item")
  public ResponseEntity<MessageResponse> buyItem(Authentication authentication,
      @RequestParam Long itemId) {
    CustomMemberDetails customMemberDetails = (CustomMemberDetails) authentication.getPrincipal();
    transactionService.buyItem(customMemberDetails, itemId);
    MessageResponse response = MessageResponse.builder()
        .message("정상적으로 처리되었습니다.").build();
    return ResponseEntity.ok().body(response);
  }

  /**
   * 요청한 회원의 구매한 아이템 List 조회
   */
  @GetMapping("/api/item/bought")
  public ResponseEntity<BoughtItemList> getBoughtItemList(@RequestParam String memberId,
      @RequestParam int page) {
    BoughtItemList boughtItemList = transactionService.getBoughtItemList(memberId, page);
    return ResponseEntity.ok(boughtItemList);
  }

  /**
   * 상점에 있는 아이템들 조회
   */
  @GetMapping("/api/item/list")
  public ResponseEntity<ItemList> getItemList(@RequestParam String memberId,
      @RequestParam int page) {
    ItemList itemList = transactionService.getItemList(memberId, page);
    return ResponseEntity.ok(itemList);
  }
}
