package com.team1.epilogue.auth.exception;

/**
 * [클래스 레벨]
 * EmailAlreadyExistException은 이미 사용 중인 이메일로 회원가입 시 발생하는 예외
 * 이 예외는 RuntimeException을 상속받으며, 기본 메시지와 HTTP 상태 코드를 포함
 */
public class EmailAlreadyExistException extends RuntimeException {

    // [필드 레벨]
    // HTTP 상태 코드를 저장하는 필드입니다.
    private final int status;

    /**
     * [생성자 레벨]
     * 기본 생성자는 "이미 등록된 이메일입니다."라는 메시지와 HTTP 상태 코드 400을 설정
     */
    public EmailAlreadyExistException() {
        super("이미 등록된 이메일입니다.");
        this.status = 400;
    }

    /**
     * [메서드 레벨]
     * getStatus 메서드는 예외에 설정된 HTTP 상태 코드를 반환
     *
     * @return HTTP 상태 코드
     */
    public int getStatus() {
        return status;
    }
}
