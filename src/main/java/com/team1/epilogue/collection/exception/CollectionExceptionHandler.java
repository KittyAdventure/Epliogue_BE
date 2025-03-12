package com.team1.epilogue.collection.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CollectionExceptionHandler {
  @ExceptionHandler(AlreadyAddedCollectionException.class)
  public ResponseEntity<String> alreadyAddedCollectionExceptionHandler(AlreadyAddedCollectionException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }
}
