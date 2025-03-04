package com.team1.epilogue.comment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommentExceptionHandler {

  /**
   * 댓글 정보가 존재하지 않을때 예외처리
   *
   * @param e CommentNotFoundException
   * @return 404
   */
  @ExceptionHandler(CommentNotFoundException.class)
  public ResponseEntity<String> handleCommentNotFoundException(CommentNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
  }

  /**
   * 댓글을 수정 / 삭제할때 권한이 없을때 예외 처리
   *
   * @param e UnauthorizedMemberException
   * @return 401
   */
  @ExceptionHandler(UnauthorizedMemberException.class)
  public ResponseEntity<String> handleUnauthorizedMemberException(UnauthorizedMemberException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
  }
}
