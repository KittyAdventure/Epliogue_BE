package com.team1.epilogue.auth.exception;

/**
 * [클래스 레벨]
 * IdAlreadyExistException은 이미 사용 중인 로그인 ID로 회원가입 시 발생하는 예외
 */
public class IdAlreadyExistException extends RuntimeException {
    public IdAlreadyExistException() {
        super("이미 등록된 사용자 ID입니다.");
    }
}
