package com.team1.epilogue.auth.exception;


public class IdAlreadyExistException extends RuntimeException {
    public IdAlreadyExistException() {
        super("이미 등록된 사용자 ID입니다.");
    }
}
