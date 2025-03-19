package com.team1.epilogue.review.dto;

import com.team1.epilogue.review.entity.Review;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 클라이언트에게 전달할 리뷰 상세 정보를 담는 DTO입니다
 */
@Getter
@Builder
@AllArgsConstructor
public class ReviewResponseDto {

  private Long id;
  private String content;
  private String nickname;
  private Long memberId;
  private String memberProfileImage;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
  private int likeCount;
  private int commentsCount;
  private String bookTitle;
  private List<String> imageUrls;

  /**
   * Review 엔티티를 DTO로 변환합니다
   *
   * @param review 변환할 Review 엔티티
   * @return 변환된 ReviewResponseDto 객체
   */
  public static ReviewResponseDto from(Review review) {
    return ReviewResponseDto.builder()
        .id(review.getId())
        .content(review.getContent())
        .nickname(review.getMember().getNickname())
        .memberId(review.getMember().getId())
        .memberProfileImage(review.getMember().getProfileUrl())
        .createdAt(review.getCreatedAt())
        .modifiedAt(review.getModifiedAt())
        .likeCount(review.getLikeCount())
        .commentsCount(review.getCommentsCount())
        .bookTitle(review.getBook().getTitle())
        .imageUrls(review.getImageUrls())
        .build();
  }
}
