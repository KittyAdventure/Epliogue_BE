package com.team1.epilogue.comment.entity;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.common.entity.BaseEntity;
import com.team1.epilogue.review.entity.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String content;

  @ManyToOne
  private Member member;

  @ManyToOne
  private Review review;

  private String color; // 댓글 색

  @Builder.Default
  @Column(nullable = false)
  private int likeCount = 0;
}
