package com.company.semocheck.exception;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorResponseDto;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionController {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity handleBaseException(GeneralException exception){
        if(exception.getMessage() != null) return ErrorResponseDto.of(exception.getErrorCode(), exception);
        else{ return  ErrorResponseDto.of(exception.getErrorCode());}

    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity handleNoHandlerFoundException(NoHandlerFoundException exception) {
        return ErrorResponseDto.of(Code.NOT_FOUND, exception);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity handleAuthException(AuthException exception){
        return ErrorResponseDto.of(exception.getErrorCode(), exception);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity handleBaseException(BadCredentialsException exception){
        return ErrorResponseDto.of(Code.UNAUTHORIZED, exception);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(Exception exception){
        log.error(exception.getMessage() + " - " + exception.getCause());
        return  ErrorResponseDto.of(Code.CONSTRAINT_NOT_VALID, exception);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity handleTransactionSystemException(Exception exception){
        log.error(exception.getMessage() + " - " + exception.getCause());
        if(exception.getCause() != null && exception.getCause().getCause() != null && exception.getCause().getCause().getClass() == ConstraintViolationException.class){
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) exception.getCause().getCause();
            return ErrorResponseDto.of(Code.CONSTRAINT_NOT_VALID, constraintViolationException);
        }

        return  ErrorResponseDto.of(Code.TRANSACTION_NOT_COMMITED, exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleBaseException(Exception exception){
        log.error(exception.getMessage() + " - " + exception.getCause());
        return ErrorResponseDto.of(Code.INTERNAL_ERROR, exception);
    }
}
