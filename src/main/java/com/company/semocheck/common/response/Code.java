package com.company.semocheck.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Code {

    OK(200, HttpStatus.OK, "Ok"),

    //JWT 토큰 에러 모음
    JWT_INVALID_SIGN(401, HttpStatus.UNAUTHORIZED, "잘못된 JWT 서명입니다."),
    JWT_EXPIRED(401, HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    JWT_UNSUPPORTED(400, HttpStatus.BAD_REQUEST, "지원되지 않는 JWT 토큰입니다."),
    JWT_INVALID(400, HttpStatus.BAD_REQUEST,  "올바른 JWT 토큰이 아닙니다."),
    JWT_PROCESS_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR,  "토큰 처리 과정 중에 에러가 발생했습니다."),

    //일반적인 API 에러 모음
    BAD_REQUEST(400, HttpStatus.BAD_REQUEST,  "잘못된 요청입니다."),
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "안증 실패"),
    FORBIDDEN(403, HttpStatus.FORBIDDEN, "권한이 없습니다."),

    NOT_FOUND(404, HttpStatus.NOT_FOUND, "존재하지 않는 리소스입니다."),

    INTERNAL_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;

    public String getMessage(String message){
        return this.getMessage() + " - " + message;
    }
}
