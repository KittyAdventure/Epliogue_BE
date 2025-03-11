package com.team1.epilogue.comment.controller;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.comment.dto.CommentPostRequest;
import com.team1.epilogue.comment.dto.CommentUpdateRequest;
import com.team1.epilogue.comment.dto.MessageResponse;
import com.team1.epilogue.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

  @PostMapping("/api/comments/{commentId}/likes")
  public ResponseEntity<MessageResponse> likeComment(Authentication authentication,
                                                     @PathVariable Long commentId) {
    CustomMemberDetails member = (CustomMemberDetails) authentication.getPrincipal();
    commentService.likeComment(member, commentId);

    MessageResponse response = MessageResponse.builder()
            .message("댓글 좋아요를 성공했습니다").build();

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/api/comments/{commentId}/likes")
  public ResponseEntity<MessageResponse> unlikeComment(Authentication authentication,
                                                       @PathVariable Long commentId) {
    CustomMemberDetails member = (CustomMemberDetails) authentication.getPrincipal();
    commentService.unlikeComment(member, commentId);

    MessageResponse response = MessageResponse.builder()
            .message("댓글 좋아요 취소를 성공했습니다").build();

    return ResponseEntity.ok(response);
  }
}
