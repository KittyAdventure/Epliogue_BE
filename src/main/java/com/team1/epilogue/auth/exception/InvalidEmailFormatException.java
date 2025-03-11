package com.team1.epilogue.auth.exception;


public class InvalidEmailFormatException extends RuntimeException {
    public InvalidEmailFormatException() {
        super("유효하지 않은 이메일 형식입니다.");
    }
}
