package com.company.semocheck.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity handleBaseException(HttpServletRequest request, BaseException exception){
        return ResponseEntity.ok(exception);
    }
}
