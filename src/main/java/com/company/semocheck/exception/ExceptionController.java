package com.company.semocheck.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity handleBaseException(HttpServletRequest request, ApiException exception){
        return ErrorResponseEntity.toResponseEntity(exception);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity handleBaseException(HttpServletRequest request, BadCredentialsException exception){
        return ErrorResponseEntity.toResponseEntity(exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleBaseException(HttpServletRequest request, Exception exception){
        return ErrorResponseEntity.toResponseEntity(exception);
    }
}
