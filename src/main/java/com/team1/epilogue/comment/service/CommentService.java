package com.team1.epilogue.comment.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.comment.dto.CommentDetail;
import com.team1.epilogue.comment.dto.CommentPostRequest;
import com.team1.epilogue.comment.dto.CommentResponse;
import com.team1.epilogue.comment.dto.CommentUpdateRequest;
import com.team1.epilogue.comment.entity.Comment;
import com.team1.epilogue.comment.exception.CommentNotFoundException;
import com.team1.epilogue.comment.exception.UnauthorizedMemberException;
import com.team1.epilogue.comment.repository.CommentRepository;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.review.exception.ReviewNotFoundException;
import com.team1.epilogue.review.repository.ReviewRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final ReviewRepository reviewRepository;

  /**
   * 댓글 작성하는 메서드
   */
  public Comment postComment(CustomMemberDetails details, CommentPostRequest dto) {
    Member member = details.getMember();

    // review 정보를 가져온다.
    Review review = reviewRepository.findById(dto.getReviewId()).orElseThrow(
        () -> new ReviewNotFoundException("존재하지 않는 리뷰입니다.")
    );

    // 댓글 갯수 +1
    reviewRepository.increaseCommentsCount(review.getId());

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
   */
  public Comment updateComment(CustomMemberDetails
      details, CommentUpdateRequest dto) {
    Member member = details.getMember();
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
   */
  @Transactional
  public void deleteComment(CustomMemberDetails details, Long commentId) {
    Member member = details.getMember();
    // 댓글 정보가 존재하지 않을 시 예외 처리
    Comment comment = commentRepository.findById(commentId).orElseThrow(
        () -> new CommentNotFoundException("존재하지 않는 댓글 정보입니다.")
    );

    // 본인의 댓글이 아닌 댓글을 삭제하려할 시 예외 처리
    if (member.getLoginId() != comment.getMember().getLoginId()) {
      throw new UnauthorizedMemberException("삭제 권한이 없는 댓글입니다.");
    }

    // 댓글 갯수 감소
    reviewRepository.decreaseCommentsCount(comment.getReview().getId());

    commentRepository.delete(comment);
  }

  /**
   * 특정 리뷰에 대한 댓글들 불러오는 기능
   * @param reviewId 조회하려는 Review 의 ID
   * @param page 페이지 번호
   * @param sort 기본적으로는 최신순 / "like" 로 들어온다면 좋아요 많은순
   */
  public CommentResponse getCommentList(Long reviewId,int page,String sort) {

    Page<Comment> comments;
    Review review = reviewRepository.findById(reviewId).orElseThrow(
        () -> new ReviewNotFoundException("존재하지 않는 리뷰입니다.")
    );
    PageRequest pageRequest = PageRequest.of(page - 1, 10);

    if (sort != null && sort.equals("like")) {
      // TODO 좋아요 순 정렬 작업해야함.
      // 아래 메서드는 컴파일을 위해 임시로 넣어놓은거
      comments = commentRepository.findCommentsByReviewSortDate(pageRequest, review);
    } else {
      // 최신 순 정렬
      comments = commentRepository.findCommentsByReviewSortDate(pageRequest, review);
    }

    List<CommentDetail> dtoList = new ArrayList<>();

    comments.getContent().stream().forEach(
        data -> {
          dtoList.add(
              CommentDetail.builder()
                  .commentId(data.getId())
                  .commentContent(data.getContent())
                  .memberId(data.getMember().getId())
                  .memberNickname(data.getMember().getNickname())
                  .memberProfile(data.getMember().getProfileUrl())
                  .commentPostDateTime(data.getCreatedAt())
//                  .commentLike(data.getCommentLikes) 댓글 좋아요 추가되면 추가해야함
                  .commentColor(data.getColor())
                  .build()
          );
        }
    );

    return CommentResponse
        .builder()
        .page(page)
        .comments(dtoList)
        .totalPages(comments.getTotalPages())
        .build();
  }
}
