package com.company.semocheck.exception;

import lombok.Getter;

@Getter
public class FilterException extends ApiException {

    public FilterException(ErrorCode errorCode) {
        super(errorCode);
    }
}
