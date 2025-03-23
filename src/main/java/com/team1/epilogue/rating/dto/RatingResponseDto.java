package com.team1.epilogue.rating.dto;

import com.team1.epilogue.rating.entity.Rating;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RatingResponseDto {

  private Long id;            // 별점 ID
  private String bookId;      // 책 ID
  private Long memberId;      // 사용자 ID
  private Double score;       // 별점
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;

  public static RatingResponseDto from(Rating rating) {
    return RatingResponseDto.builder()
        .id(rating.getId())
        .bookId(rating.getBook().getId())
        .memberId(rating.getMember().getId())
        .score(rating.getScore())
        .createdAt(rating.getCreatedAt())
        .modifiedAt(rating.getModifiedAt())
        .build();
  }
}
