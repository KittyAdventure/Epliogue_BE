package com.team1.epilogue.transaction.exception;

public class TransactionNotFoundException extends RuntimeException {

  public TransactionNotFoundException(String message) {
    super(message);
  }
}
