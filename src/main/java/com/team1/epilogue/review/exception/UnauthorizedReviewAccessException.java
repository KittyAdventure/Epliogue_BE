package com.team1.epilogue.review.exception;

/**
 * 리뷰 수정 또는 삭제 권한이 없는 사용자가 접근할 때 발생하는 예외
 */
public class UnauthorizedReviewAccessException extends RuntimeException {

  public UnauthorizedReviewAccessException(String message) {
    super(message);
  }
}
