package com.company.semocheck.exception;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionController {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity handleBaseException(HttpServletRequest request, GeneralException exception){
        if(exception.getMessage() != null) return ErrorResponseDto.of(exception.getErrorCode(), exception);
        else{ return  ErrorResponseDto.of(exception.getErrorCode());}

    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity handleNoHandlerFoundException(NoHandlerFoundException exception,
                                                                          HttpServletRequest request) {
        return ErrorResponseDto.of(Code.NOT_FOUND, exception);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity handleAuthException(HttpServletRequest request, AuthException exception){
        return ErrorResponseDto.of(exception.getErrorCode(), exception);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity handleBaseException(HttpServletRequest request, BadCredentialsException exception){
        return ErrorResponseDto.of(Code.UNAUTHORIZED, exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleBaseException(HttpServletRequest request, Exception exception){
        log.error(exception.getMessage() + " - " + exception.getCause());
        return ErrorResponseDto.of(Code.INTERNAL_ERROR, exception);
    }
}
