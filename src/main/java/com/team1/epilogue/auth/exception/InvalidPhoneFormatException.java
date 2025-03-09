package com.team1.epilogue.auth.exception;


public class InvalidPhoneFormatException extends RuntimeException {
    public InvalidPhoneFormatException() {super("전화번호 형식에 맞지 않습니다.");}
}
