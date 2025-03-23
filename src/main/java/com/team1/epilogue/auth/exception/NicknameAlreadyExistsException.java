package com.team1.epilogue.auth.exception;

public class NicknameAlreadyExistsException extends RuntimeException {
    public NicknameAlreadyExistsException() {
        super("닉네임이 이미 사용 중입니다.");
    }
}
