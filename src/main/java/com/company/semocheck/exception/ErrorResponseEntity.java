package com.company.semocheck.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

@Data
@Builder
public class ErrorResponseEntity {
    private int status;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ApiException e){
        return ResponseEntity
                .status(e.getStatus())
                .body(ErrorResponseEntity.builder()
                        .status(e.getStatus().value())
                        .code(e.getStatus().name())
                        .message(e.getMessage())
                        .build());
    }

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(BadCredentialsException e){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseEntity.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .code(HttpStatus.BAD_REQUEST.name())
                        .message("Invalid email or password")
                        .build());
    }

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(Exception e){
        return ResponseEntity
                .status(ErrorCode.UNKNOWN_ERROR.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .status(ErrorCode.UNKNOWN_ERROR.getHttpStatus().value())
                        .code(ErrorCode.UNKNOWN_ERROR.name())
                        .message(e.getMessage())
                        .build());
    }
}
