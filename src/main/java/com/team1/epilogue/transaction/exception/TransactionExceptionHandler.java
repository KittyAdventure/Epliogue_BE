package com.team1.epilogue.transaction.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TransactionExceptionHandler {

  @ExceptionHandler(AlreadyBoughtItemException.class)
  public ResponseEntity<String> handleAlreadyBoughtItemException(AlreadyBoughtItemException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<String> handleInvalidRequestException(InvalidRequestException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler(InvalidTransactionException.class)
  public ResponseEntity<String> handleInvalidTransactionException(InvalidTransactionException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler(ItemNotFoundException.class)
  public ResponseEntity<String> handleItemNotFoundException(ItemNotFoundException e) {
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(TransactionNotFoundException.class)
  public ResponseEntity<String> handleTransactionNotFoundException(TransactionNotFoundException e) {
    return ResponseEntity.notFound().build();
  }
}
