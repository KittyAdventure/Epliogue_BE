package com.team1.epilogue.auth.exception;

/**
 * [클래스 레벨]
 * 회원가입 관련 예외를 처리하기 위한 커스텀 예외 클래스
 */
public class InvalidPhoneFormatException extends RuntimeException {
    public InvalidPhoneFormatException() {super("전화번호 형식에 맞지 않습니다.");}
}
