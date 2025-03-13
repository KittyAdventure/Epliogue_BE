package com.team1.epilogue.follow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * [클래스 레벨]
 * 요청한 회원을 찾을 수 없을 때 발생하는 예외
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException() {
        super("대상 사용자가 존재하지 않습니다.");
    }
}
