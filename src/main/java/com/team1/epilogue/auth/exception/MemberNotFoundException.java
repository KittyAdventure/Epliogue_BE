package com.team1.epilogue.auth.exception;

/**
 * [클래스 레벨]
 * MemberNotFoundException은 회원 정보를 찾을 수 없을 때 발생하는 예외
 */
public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super("회원 정보를 찾을 수 없습니다.");
    }

    // 메시지를 전달받는 생성자 추가
    public MemberNotFoundException(String message) {
        super(message);
    }
}
