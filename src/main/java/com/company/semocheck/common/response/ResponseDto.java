package com.company.semocheck.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ResponseDto {

    private final Boolean success;
    private final Integer code;
    private final String message;

    public static ResponseDto of(Boolean success, String message){
        return new ResponseDto(success, Code.OK.getCode(), message);
    }

    public static ResponseDto of(Boolean success, Code code) {
        return new ResponseDto(success, code.getCode(), code.getMessage());
    }

    public static ResponseDto of(Boolean success, Code code, String message) {
        return new ResponseDto(success, code.getCode(), code.getMessage(message));
    }
}
