package com.team1.epilogue.review.exception;

/**
 * 특정 책을 찾을 수 없을 때 발생하는 예외
 */
public class BookNotFoundException extends RuntimeException {

  public BookNotFoundException(String message) {
    super(message);
  }
}
