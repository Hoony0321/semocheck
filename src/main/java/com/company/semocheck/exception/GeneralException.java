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
        this.errorCode = errorCode;
    }

    public GeneralException(String message){
        super(message);
        this.errorCode = Code.INTERNAL_ERROR;
    }

    public GeneralException(Code errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }
}
