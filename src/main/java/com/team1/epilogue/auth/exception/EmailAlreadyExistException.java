package com.team1.epilogue.auth.exception;


public class EmailAlreadyExistException extends RuntimeException {

    private final int status;

    public EmailAlreadyExistException() {
        super("이미 등록된 이메일입니다.");
        this.status = 400;
    }

    public int getStatus() {
        return status;
    }
}
