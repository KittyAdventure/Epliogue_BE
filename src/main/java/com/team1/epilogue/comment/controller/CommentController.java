package com.team1.epilogue.comment.controller;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.comment.dto.CommentPostRequest;
import com.team1.epilogue.comment.dto.CommentUpdateRequest;
import com.team1.epilogue.comment.service.CommentService;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
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
  public ResponseEntity<HashMap<String, String>> postComment(Authentication authentication,
      @RequestBody CommentPostRequest dto) {
    Member member = (Member) authentication.getPrincipal();
    commentService.postComment(member, dto);

    HashMap<String, String> map = new HashMap<>();
    map.put("message", "댓글이 성공적으로 작성 되었습니다");

    return ResponseEntity.ok(map);
  }

  /**
   * 댓글 수정하는 메서드입니다
   *
   * @param authentication 인증된 사용자정보
   * @param dto            수정하려는 댓글 정보가 담긴 DTO
   */
  @PutMapping("/api/comments")
  public ResponseEntity<HashMap<String, String>> updateComment(Authentication authentication,
      @RequestBody CommentUpdateRequest dto) {
    Member member = (Member) authentication.getPrincipal();
    commentService.updateComment(member, dto);

    HashMap<String, String> map = new HashMap<>();
    map.put("message", "댓글이 성공적으로 작성 되었습니다");

    return ResponseEntity.ok(map);
  }

  /**
   * 댓글 삭제기능
   *
   * @param authentication 인증된 사용자정보
   * @param commentId      삭제하려는 댓글 정보가 담긴 DTO
   */
  @DeleteMapping("/api/comments")
  public ResponseEntity<?> deleteComment(Authentication authentication,
      @RequestParam Long commentId) {
    Member member = (Member) authentication.getPrincipal();
    commentService.deleteComment(member, commentId);

    HashMap<String, String> map = new HashMap<>();
    map.put("message", "댓글이 성공적으로 삭제 되었습니다");

    return ResponseEntity.ok(map);
  }
}
