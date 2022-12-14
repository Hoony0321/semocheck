package com.company.semocheck.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    //JWT 토큰 에러 모음
    JWT_INVALID_SIGN(HttpStatus.UNAUTHORIZED, "잘못된 JWT 서명입니다."),

    JWT_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 JWT 토큰입니다."),

    JWT_UNSUPPORTED(HttpStatus.NOT_ACCEPTABLE, "지원되지 않는 JWT 토큰입니다."),

    JWT_INVALID(HttpStatus.BAD_REQUEST, "올바른 JWT 토큰이 아닙니다."),

    JWT_PROCESS_ERROR(HttpStatus.UNAUTHORIZED, "토큰 처리 과정 중에 에러가 발생했습니다."),

    //API 에러 모음
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "해당 URL에 대한 접근 권한이 없습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 접근 방법입니다."),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 에러입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
