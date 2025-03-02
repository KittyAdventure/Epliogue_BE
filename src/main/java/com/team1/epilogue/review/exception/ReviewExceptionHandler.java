package com.team1.epilogue.review.exception;

import com.team1.epilogue.rating.exception.RatingNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 리뷰 관련 예외를 처리하는 핸들러
 */
@RestControllerAdvice
public class ReviewExceptionHandler {

    /**
     * 책을 찾을 수 없을 때 예외 처리
     */
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<String> handleBookNotFoundException(BookNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**
     * 리뷰를 찾을 수 없을 때 예외 처리
     */
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<String> handleReviewNotFoundException(ReviewNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**
     * 리뷰 작성자가 아닌 사용자가 수정/삭제하려 할 때 예외 처리
     */
    @ExceptionHandler(UnauthorizedReviewAccessException.class)
    public ResponseEntity<String> handleUnauthorizedReviewAccessException(UnauthorizedReviewAccessException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    /**
     * 해당 별점을 찾을 수 없을 때 예외 처리
     */
    @ExceptionHandler(RatingNotFoundException.class)
    public ResponseEntity<String> handleRatingNotFoundException(RatingNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
