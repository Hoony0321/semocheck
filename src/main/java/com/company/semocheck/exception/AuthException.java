package com.company.semocheck.exception;

import com.company.semocheck.common.response.Code;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException{

    private final Code errorCode;

    public AuthException(Code errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }
}
