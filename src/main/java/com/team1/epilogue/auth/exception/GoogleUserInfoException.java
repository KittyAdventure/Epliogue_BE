package com.team1.epilogue.auth.exception;

public class GoogleUserInfoException extends RuntimeException {
    public GoogleUserInfoException(String message) {
        super(message);
    }

    public GoogleUserInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoogleUserInfoException() {
        super("Google 사용자 정보를 가져오지 못했습니다.");
    }

    public GoogleUserInfoException(Throwable cause) {
        super("Google API 통신 중 오류가 발생했습니다.", cause);
    }

    public static GoogleUserInfoException fromClientError(Throwable cause) {
        return new GoogleUserInfoException("Google API 클라이언트 오류 발생", cause);
    }

    public static GoogleUserInfoException fromStatusCode(int statusCode, String responseBody) {
        String message = String.format("Google API 요청 중 오류 발생. 상태 코드: %d, 응답: %s", statusCode, responseBody);
        return new GoogleUserInfoException(message);
    }

    public static GoogleUserInfoException fromServerError(Throwable cause) {
        return new GoogleUserInfoException("Google API 서버 오류 발생", cause);
    }

    public static GoogleUserInfoException fromTimeoutOrNetworkError(Throwable cause) {
        return new GoogleUserInfoException("Google API 요청이 타임아웃되었거나 네트워크 오류가 발생했습니다.", cause);
    }

    public static GoogleUserInfoException fromUnknownError(Throwable cause) {
        return new GoogleUserInfoException("알 수 없는 Google API 오류가 발생했습니다.", cause);
    }

    // 서비스 코드에서 사용할 수 있도록 추가적인 정적 팩토리 메서드 정의
    public static GoogleUserInfoException fromEmptyResponse() {
        return new GoogleUserInfoException("Google 사용자 정보 응답이 비어 있습니다.");
    }

    public static GoogleUserInfoException fromClientIdMismatch() {
        return new GoogleUserInfoException("Google Client ID가 일치하지 않습니다.");
    }
}
