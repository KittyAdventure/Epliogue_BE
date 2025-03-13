package com.team1.epilogue.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingRequiredFieldException extends RuntimeException {
    public MissingRequiredFieldException() {super("필수입력 항목입니다.");}
}
