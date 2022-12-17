package com.company.semocheck.common.response;

import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class ErrorResponseDto extends ResponseDto{

    private ErrorResponseDto(Code errorCode){
        super(false, errorCode.getCode(), errorCode.getMessage());
    }

    private ErrorResponseDto(Code errorCode, Exception e){
        super(false, errorCode.getCode(), errorCode.getMessage(e.getMessage()));
    }

    private ErrorResponseDto(Code errorCode, String message){
        super(false, errorCode.getCode(), errorCode.getMessage(message));
    }

    public static ResponseEntity<ErrorResponseDto> of(Code errorCode){
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ErrorResponseDto(errorCode));
    }

    public static ResponseEntity<ErrorResponseDto> of(Code errorCode, Exception e){
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ErrorResponseDto(errorCode, e.getMessage()));
    }

    public static ResponseEntity<ErrorResponseDto> of(Code errorCode, String message,  Exception e){
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ErrorResponseDto(errorCode, message));
    }

}
