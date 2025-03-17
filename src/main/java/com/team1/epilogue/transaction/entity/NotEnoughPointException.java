package com.team1.epilogue.transaction.entity;

public class NotEnoughPointException extends RuntimeException {

  public NotEnoughPointException(String message) {
    super(message);
  }
}
