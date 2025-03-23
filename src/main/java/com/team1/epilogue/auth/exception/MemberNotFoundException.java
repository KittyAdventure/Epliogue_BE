package com.team1.epilogue.auth.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super("회원 정보를 찾을 수 없습니다.");
    }

    // 메시지를 전달받는 생성자 추가
    public MemberNotFoundException(String message) {
        super(message);
    }
}
