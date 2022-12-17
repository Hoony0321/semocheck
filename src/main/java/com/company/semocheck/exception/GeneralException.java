package com.company.semocheck.exception;

import com.company.semocheck.common.response.Code;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException{

    private final Code errorCode;

    public GeneralException(){
        super(Code.INTERNAL_ERROR.getMessage());
        this.errorCode = Code.INTERNAL_ERROR;
    }

    public GeneralException(Code errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public GeneralException(String message){
        super(Code.INTERNAL_ERROR.getMessage(message));
        this.errorCode = Code.INTERNAL_ERROR;
    }

    public GeneralException(Code errorCode, String message){
        super(errorCode.getMessage(message));
        this.errorCode = errorCode;
    }
}
