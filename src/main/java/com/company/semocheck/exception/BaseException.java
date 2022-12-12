package com.company.semocheck.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException{
    private HttpStatus status;
    private String message;

    public BaseException(ErrorCode errorCode){
        this.status = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
    }

}
