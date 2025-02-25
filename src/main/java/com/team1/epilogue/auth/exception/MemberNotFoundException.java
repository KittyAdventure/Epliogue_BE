package com.team1.epilogue.auth.exception;

/**
 * [클래스 레벨]
 * MemberNotFoundException은 존재하지 않는 회원 정보를 요청할 때 발생하는 예외
 */
public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super("존재하지 않는 회원입니다.");
    }
}
