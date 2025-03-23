package com.team1.epilogue.rating.controller;

import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.rating.dto.RatingRequestDto;
import com.team1.epilogue.rating.dto.RatingResponseDto;
import com.team1.epilogue.rating.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RatingController {

  private final RatingService ratingService;

  /**
   * 책에 대한 별점을 생성하거나 수정합니다
   *
   * @param bookId           별점을 남길 책의 ID
   * @param ratingRequestDto 클라이언트가 전달한 별점 데이터
   * @param authentication   현재 인증된 사용자
   * @return 생성된 별점의 상세 정보를 담은 DTO
   */
  @PostMapping("/books/{bookId}/ratings")
  public ResponseEntity<RatingResponseDto> createRating(@PathVariable String bookId,
      @RequestBody @Valid RatingRequestDto ratingRequestDto,
      Authentication authentication) {
    CustomMemberDetails member = (CustomMemberDetails) authentication.getPrincipal();
    RatingResponseDto ratingResponseDto =
        ratingService.createRating(bookId, ratingRequestDto, member);

    return ResponseEntity.ok().body(ratingResponseDto);
  }

  /**
   * 책에 대한 별점을 수정합니다
   *
   * @param bookId           별점을 수정할 책의 ID
   * @param ratingRequestDto 클라이언트가 전달한 수정된 별점 데이터
   * @param authentication   현재 인증된 사용자
   * @return 수정된 별점의 상세 정보를 담은 DTO
   */
  @PutMapping("/books/{bookId}/ratings")
  public ResponseEntity<RatingResponseDto> updateRating(@PathVariable String bookId,
      @RequestBody RatingRequestDto ratingRequestDto,
      Authentication authentication) {
    CustomMemberDetails member = (CustomMemberDetails) authentication.getPrincipal();
    RatingResponseDto ratingResponseDto =
        ratingService.updateRating(bookId, ratingRequestDto, member);

    return ResponseEntity.ok().body(ratingResponseDto);
  }

  /**
   * 책에 대한 별점을 삭제합니다
   *
   * @param bookId         별점을 삭제할 책의 ID
   * @param authentication 현재 인증된 사용자
   * @return 삭제 완료 메시지
   */
  @DeleteMapping("/books/{bookId}/ratings")
  public ResponseEntity<String> deleteRating(@PathVariable String bookId,
      Authentication authentication) {
    CustomMemberDetails member = (CustomMemberDetails) authentication.getPrincipal();
    ratingService.deleteRating(bookId, member);

    return ResponseEntity.ok().body("별점이 성공적으로 삭제되었습니다.");
  }
}
