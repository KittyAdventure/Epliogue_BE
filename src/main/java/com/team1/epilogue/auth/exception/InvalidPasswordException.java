package com.team1.epilogue.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * [클래스 레벨]
 * 비밀번호가 6자리 미만일 때 발생하는 예외
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {super("비밀번호는 최소 6자리 이상이어야 합니다.");}
}