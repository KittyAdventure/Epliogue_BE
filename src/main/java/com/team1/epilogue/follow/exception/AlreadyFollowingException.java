package com.team1.epilogue.follow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * [클래스 레벨]
 * 이미 팔로우한 회원을 다시 팔로우하려고 할 때 발생하는 예외
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyFollowingException extends RuntimeException {

    public AlreadyFollowingException() {
        super("이미 팔로우 상태입니다.");
    }
}
