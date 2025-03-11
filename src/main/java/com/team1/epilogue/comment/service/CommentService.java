package com.team1.epilogue.comment.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.comment.dto.CommentPostRequest;
import com.team1.epilogue.comment.dto.CommentUpdateRequest;
import com.team1.epilogue.comment.entity.Comment;
import com.team1.epilogue.comment.entity.CommentLike;
import com.team1.epilogue.comment.exception.CommentNotFoundException;
import com.team1.epilogue.comment.exception.UnauthorizedMemberException;
import com.team1.epilogue.comment.repository.CommentLikeRepository;
import com.team1.epilogue.comment.repository.CommentRepository;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.review.exception.AlreadyLikedException;
import com.team1.epilogue.review.exception.LikeNotFoundException;
import com.team1.epilogue.review.exception.ReviewNotFoundException;
import com.team1.epilogue.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final ReviewRepository reviewRepository;
  private final CommentLikeRepository commentLikeRepository;

  /**
   * 댓글 작성하는 메서드
   * @param member 사용자 정보를 담은 객체
   * @param dto 작성할 댓글의 정보를 담은 DTO
   * @return 저장된 Comment return
   */
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

  /**
   * 댓글 수정하는 메서드
   * @param member 사용자 정보를 담은 member 객체
   * @param dto 수정할 댓글의 정보를 담은 dto 객체
   * @return 수정된 Comment return
   */
  public Comment updateComment(Member member, CommentUpdateRequest dto) {
    // 댓글 정보가 존재하지 않을 시 예외 처리
    Comment comment = commentRepository.findById(dto.getCommentId()).orElseThrow(
        () -> new CommentNotFoundException("존재하지 않는 댓글 정보입니다.")
    );
    // 본인의 댓글이 아닌 댓글을 수정하려할 시 예외 처리
    if (member.getLoginId() != comment.getMember().getLoginId()) {
      throw new UnauthorizedMemberException("수정 권한이 없는 댓글입니다.");
    }
    comment.toBuilder().content(dto.getContent());
    return commentRepository.save(comment);
  }

  /**
   * 댓글 삭제하는 기능
   * @param member 사용자 정보를 담은 Member 객체
   * @param commentId 삭제하려는 댓글의 PK
   */
  @Transactional
  public void deleteComment(Member member, Long commentId) {
    // 댓글 정보가 존재하지 않을 시 예외 처리
    Comment comment = commentRepository.findById(commentId).orElseThrow(
        () -> new CommentNotFoundException("존재하지 않는 댓글 정보입니다.")
    );

    // 본인의 댓글이 아닌 댓글을 삭제하려할 시 예외 처리
    if (member.getLoginId() != comment.getMember().getLoginId()) {
      throw new UnauthorizedMemberException("삭제 권한이 없는 댓글입니다.");
    }
    commentRepository.delete(comment);
  }

  @Transactional
  public void likeComment(CustomMemberDetails details, Long commentId) {
    Member member = details.getMember();
    Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다"));

    if (commentLikeRepository.existsByCommentIdAndMemberId(commentId, member.getId())) {
      throw new AlreadyLikedException("이미 좋아요를 눌렀습니다.");
    }

    CommentLike commentLike = new CommentLike(comment, member);
    commentLikeRepository.save(commentLike);

    commentRepository.increaseLikeCount(commentId);
  }

  @Transactional
  public void unlikeComment(CustomMemberDetails details, Long commentId) {
    Member member = details.getMember();
    CommentLike commentLike = commentLikeRepository.findByCommentIdAndMemberId(commentId, member.getId())
            .orElseThrow(() -> new LikeNotFoundException("취소할 좋아요가 없습니다."));

    commentLikeRepository.delete(commentLike);

    commentRepository.decreaseLikeCount(commentId);
  }
}
