package com.team1.epilogue.follow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * [클래스 레벨]
 * 인증되지 않은 사용자가 요청을 보낼 경우 발생하는 예외
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException() {
        super("잘못된 인증");
    }
}
