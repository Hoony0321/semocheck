package com.company.semocheck.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ApiException extends RuntimeException{
    private HttpStatus status;
    private String message;

    public ApiException(ErrorCode errorCode){
        this.status = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
    }

}
