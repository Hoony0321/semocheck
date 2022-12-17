package com.company.semocheck.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Code {

    OK(200, HttpStatus.OK, "Ok"),

    //JWT 토큰 에러 모음
    JWT_INVALID_SIGN(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, "잘못된 JWT 서명입니다."),
    JWT_EXPIRED(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, "만료된 JWT 토큰입니다."),
    JWT_UNSUPPORTED(HttpStatus.NOT_ACCEPTABLE.value(), HttpStatus.NOT_ACCEPTABLE, "지원되지 않는 JWT 토큰입니다."),
    JWT_INVALID(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST,  "올바른 JWT 토큰이 아닙니다."),
    JWT_PROCESS_ERROR(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED,  "토큰 처리 과정 중에 에러가 발생했습니다."),

    //API 에러 모음
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE.value(), HttpStatus.NOT_ACCEPTABLE,  "해당 리스소에 대해 접근할 수 없습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST,  "잘못된 접근 방법입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, "존재하지 않는 리소스입니다."),
    ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, "argument error"),


    VALIDATION_ERROR(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, "검증 오류입니다."),



    DATA_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, "데이터 접근 에러입니다."),

    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "권한이 없습니다."),

    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,  "알 수 없는 에러입니다.");


    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;

    public String getMessage(String message){
        return this.getMessage() + " - " + message;
    }
}
