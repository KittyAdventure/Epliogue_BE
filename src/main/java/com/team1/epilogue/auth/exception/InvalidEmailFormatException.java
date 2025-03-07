package com.team1.epilogue.auth.exception;

/**
 * [클래스 레벨]
 * EmailNotValidException은 유효하지 않은 이메일 형식이 입력될 때 발생하는 런타임 예외
 */
public class InvalidEmailFormatException extends RuntimeException {
    public InvalidEmailFormatException() {
        super("유효하지 않은 이메일 형식입니다.");
    }
}
