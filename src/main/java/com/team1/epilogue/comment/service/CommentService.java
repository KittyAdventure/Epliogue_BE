package com.team1.epilogue.comment.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.comment.dto.CommentPostRequest;
import com.team1.epilogue.comment.entity.Comment;
import com.team1.epilogue.comment.repository.CommentRepository;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.review.exception.ReviewNotFoundException;
import com.team1.epilogue.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final ReviewRepository reviewRepository;

  public Comment postComment(Member member, CommentPostRequest dto) {
    // review 정보를 가져온다.
    Review review = reviewRepository.findById(dto.getReviewId()).orElseThrow(
        () -> new ReviewNotFoundException("존재하지 않는 리뷰입니다.")
    );

    // 댓글을 저장한다.
    return commentRepository.save(Comment.builder()
        .content(dto.getContent())
        .member(member)
        .review(review)
        .color(member.getCommentColor() == null ? null
            : member.getCommentColor().toString()) // 사용자가 장착중인 댓글 색 아이템을 불러온다
        .build());

  }
}
