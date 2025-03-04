package com.team1.epilogue.auth.exception;

public class KakaoUserInfoException extends RuntimeException {

    public KakaoUserInfoException(String message) {
        super(message);
    }

    public KakaoUserInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public KakaoUserInfoException() {
        super("카카오 사용자 정보를 가져오지 못했습니다.");
    }

    public KakaoUserInfoException(Throwable cause) {
        super("카카오 API 통신 중 오류 발생", cause);
    }

    public KakaoUserInfoException(int statusCode, String responseBody) {
        super(String.format("카카오 API 요청 중 오류 발생. 상태 코드: %d, 응답: %s", statusCode, responseBody));
    }

    public static KakaoUserInfoException fromStatusCode(int statusCode, String responseBody) {
        String message = String.format("카카오 API 요청 중 오류 발생. 상태 코드: %d, 응답: %s", statusCode, responseBody);
        return new KakaoUserInfoException(message);
    }

    public static KakaoUserInfoException fromClientError(Throwable cause) {
        return new KakaoUserInfoException("카카오 API 클라이언트 오류 발생", cause);
    }

    public static KakaoUserInfoException fromServerError(Throwable cause) {
        return new KakaoUserInfoException("카카오 API 서버 오류 발생", cause);
    }

    public static KakaoUserInfoException fromTimeoutOrNetworkError(Throwable cause) {
        return new KakaoUserInfoException("카카오 API 요청이 타임아웃되었거나 네트워크 오류가 발생했습니다.", cause);
    }
//
    public static KakaoUserInfoException fromUnknownError(Throwable cause) {
        return new KakaoUserInfoException("알 수 없는 카카오 API 오류가 발생했습니다.", cause);
    }

    public static KakaoUserInfoException fromEmptyResponse() {
        return new KakaoUserInfoException("카카오 사용자 정보 응답이 비어 있습니다.");
    }
}

