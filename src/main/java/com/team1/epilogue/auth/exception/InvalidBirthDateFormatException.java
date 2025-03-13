package com.team1.epilogue.auth.exception;

public class InvalidBirthDateFormatException extends RuntimeException {
    public InvalidBirthDateFormatException() {
        super("생년월일 형식은 YYYY-MM-DD 여야 합니다.");
    }
}