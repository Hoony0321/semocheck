package com.company.semocheck.exception;

import com.company.semocheck.common.response.Code;
import lombok.Getter;

@Getter
public class FilterException extends RuntimeException {

    private final Code errorCode;

    public FilterException(){
        super(Code.INTERNAL_ERROR.getMessage());
        this.errorCode = Code.INTERNAL_ERROR;
    }

    public FilterException(Code errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public FilterException(String message){
        super(Code.INTERNAL_ERROR.getMessage(message));
        this.errorCode = Code.INTERNAL_ERROR;
    }

    public FilterException(Code errorCode, String message){
        super(errorCode.getMessage(message));
        this.errorCode = errorCode;
    }
}
