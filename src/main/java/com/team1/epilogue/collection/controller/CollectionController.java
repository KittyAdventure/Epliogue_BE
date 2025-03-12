package com.team1.epilogue.collection.controller;

import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.collection.dto.CollectionResponse;
import com.team1.epilogue.collection.dto.StringResponse;
import com.team1.epilogue.collection.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CollectionController {

  private final CollectionService collectionService;

  /**
   * 본인의 컬렉션을 불러오는 메서드
   */
  @GetMapping("/api/collection")
  public ResponseEntity<CollectionResponse> getCollection(
      Authentication authentication,
      @RequestParam int page) {
    CustomMemberDetails member = (CustomMemberDetails) authentication.getPrincipal();
    CollectionResponse collection = collectionService.getCollection(member, page);

    return ResponseEntity.ok(collection);
  }

  /**
   * 컬렉션 삭제하는 메서드
   */
  @DeleteMapping("/api/collection")
  public ResponseEntity<StringResponse> deleteCollection(
      Authentication authentication,
      @RequestParam String bookId
  ) {
    CustomMemberDetails member = (CustomMemberDetails) authentication.getPrincipal();
    collectionService.deleteCollection(bookId, member);
    return ResponseEntity.ok(StringResponse.builder().message("정상적으로 삭제 되었습니다.").build());
  }

  /**
   * 컬렉션 추가하는 메서드
   */
  @PostMapping("/api/collection")
  public ResponseEntity<StringResponse> addCollection(
      Authentication authentication,
      @RequestParam String bookId
  ) {
    CustomMemberDetails member = (CustomMemberDetails) authentication.getPrincipal();
    collectionService.addCollection(bookId, member);
    return ResponseEntity.ok(StringResponse.builder().message("정상적으로 추가 되었습니다.").build());
  }
}
