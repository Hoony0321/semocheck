package com.company.semocheck.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ResponseDto {

    @Schema(description = "성공 여부", defaultValue = "true")
    private final Boolean success;

    @Schema(description = "상태 코드 이름", defaultValue = "Ok")
    private final String code;

    @Schema(description = "성공 메세지")
    private final String message;

    public static ResponseDto of(Boolean success, Code code) {
        return new ResponseDto(success, code.name(), code.getMessage());
    }

//    public static ResponseDto of(Boolean success, Code code, String message) {
//        return new ResponseDto(success, code.name(), code.getMessage(message));
//    }
}
