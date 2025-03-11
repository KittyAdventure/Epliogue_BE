package com.team1.epilogue.follow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * [클래스 레벨]
 * 팔로우 관계가 존재하지 않을 때 발생하는 예외
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FollowNotFoundException extends RuntimeException {

    public FollowNotFoundException() {
        super("팔로우 관계가 존재하지 않습니다.");
    }
}
