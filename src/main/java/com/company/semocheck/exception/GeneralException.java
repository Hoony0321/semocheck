package com.company.semocheck.exception;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
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

    public GeneralException(Code errorCode, ErrorMessages errorMessages){
        super(errorMessages.getMessage());
        this.errorCode = errorCode;
    }
}
