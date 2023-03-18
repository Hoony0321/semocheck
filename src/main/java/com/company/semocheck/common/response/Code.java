package com.company.semocheck.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Code {

    //SUCCESS CODE 모음
    SUCCESS(200, HttpStatus.OK, "성공"),
    SUCCESS_READ(200, HttpStatus.OK, "조회 성공"),
    SUCCESS_CREATE(200, HttpStatus.OK, "생성 성공"),
    SUCCESS_ADD(200, HttpStatus.OK, "추가 성공"),
    SUCCESS_DELETE(200, HttpStatus.OK, "삭제 성공"),
    SUCCESS_UPDATE(200, HttpStatus.OK, "수정 성공"),


    //JWT CODE 모음
    JWT_SUCCESS_REFRESH(200, HttpStatus.OK, "재발급 성공"),
    JWT_INVALID_SIGN(401, HttpStatus.UNAUTHORIZED, "잘못된 서명"),
    JWT_EXPIRED(401, HttpStatus.UNAUTHORIZED, "만료된 토큰"),
    JWT_UNSUPPORTED(400, HttpStatus.BAD_REQUEST, "지원되지 않는 토큰"),
    JWT_INVALID(400, HttpStatus.BAD_REQUEST,  "올바르지 않은 토큰."),
    JWT_PROCESS_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR,  "토큰 처리 에러"),

    //일반적인 API 에러 모음
    BAD_REQUEST(400, HttpStatus.BAD_REQUEST,  "잘못된 요청"),
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "안증 실패"),
    FORBIDDEN(403, HttpStatus.FORBIDDEN, "권한 없음"),

    NOT_FOUND(404, HttpStatus.NOT_FOUND, "존재하지 않는 리소스"),

    CONSTRAINT_NOT_VALID(410, HttpStatus.BAD_REQUEST, "제약조건 위배"),
    TRANSACTION_NOT_COMMITED(411, HttpStatus.BAD_REQUEST, "트랜잭션 커밋 실패"),

    INTERNAL_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류 발생");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;

    public String getMessage(String message){
        return this.getMessage() + " - " + message;
    }
}
