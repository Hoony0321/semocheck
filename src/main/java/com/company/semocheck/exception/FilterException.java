package com.company.semocheck.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public class FilterException extends BaseException {

    public FilterException(ErrorCode errorCode) {
        super(errorCode);
    }
}
