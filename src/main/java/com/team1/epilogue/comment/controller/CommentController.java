package com.team1.epilogue.comment.controller;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.comment.dto.CommentPostRequest;
import com.team1.epilogue.comment.dto.CommentResponse;
import com.team1.epilogue.comment.dto.CommentUpdateRequest;
import com.team1.epilogue.comment.dto.MessageResponse;
import com.team1.epilogue.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  /**
   * 댓글 작성하는 메서드입니다
   *
   * @param authentication 인증된 사용자 정보
   * @param dto            작성하려는 댓글 정보가 담긴 DTO
   * @return 성공시 String 값 응답
   */
  @PostMapping("/api/comments")
  public ResponseEntity<MessageResponse> postComment(Authentication authentication,
      @RequestBody CommentPostRequest dto) {
    Member member = (Member) authentication.getPrincipal();
    commentService.postComment(member, dto);

    MessageResponse response = MessageResponse.builder()
        .message("댓글이 성공적으로 작성되었습니다.").build();

    return ResponseEntity.ok(response);
  }

  /**
   * 댓글 수정하는 메서드입니다
   *
   * @param authentication 인증된 사용자정보
   * @param dto            수정하려는 댓글 정보가 담긴 DTO
   */
  @PutMapping("/api/comments")
  public ResponseEntity<MessageResponse> updateComment(Authentication authentication,
      @RequestBody CommentUpdateRequest dto) {
    Member member = (Member) authentication.getPrincipal();
    commentService.updateComment(member, dto);

    MessageResponse response = MessageResponse.builder()
        .message("댓글이 성공적으로 수정되었습니다.").build();

    return ResponseEntity.ok(response);
  }

  /**
   * 댓글 삭제기능
   *
   * @param authentication 인증된 사용자정보
   * @param commentId      삭제하려는 댓글 정보가 담긴 DTO
   */
  @DeleteMapping("/api/comments")
  public ResponseEntity<MessageResponse> deleteComment(Authentication authentication,
      @RequestParam Long commentId) {
    Member member = (Member) authentication.getPrincipal();
    commentService.deleteComment(member, commentId);

    MessageResponse response = MessageResponse.builder()
        .message("댓글이 성공적으로 삭제되었습니다.").build();

    return ResponseEntity.ok(response);
  }

  /**
   * 특정 리뷰에 대한 댓글들을 보는 메서드
   * @param reviewId 조회하려는 review Id
   * @param page 페이지 번호
   * @param sort 기본값 = 최신순 / "like" = 좋아요 많은 순
   */
  @GetMapping("/api/comments/view")
  public ResponseEntity<CommentResponse> getCommentList(
      @RequestParam Long reviewId,
      @RequestParam int page,
      @RequestParam String sort) {
    CommentResponse commentList = commentService.getCommentList(reviewId, page, sort);
    return ResponseEntity.ok(commentList);
  }
}
