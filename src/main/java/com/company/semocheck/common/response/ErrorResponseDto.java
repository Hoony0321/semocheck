package com.company.semocheck.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

@Getter
@ToString
@RequiredArgsConstructor
public class ErrorResponseDto{

    @Schema(description = "성공 여부", defaultValue = "false")
    private final Boolean success = false;

    @Schema(description = "상태 코드 이름", example = "error code name")
    private final String code;

    @Schema(description = "에러 메세지")
    private final String message;


    private ErrorResponseDto(Code errorCode){
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
    }

    private ErrorResponseDto(Code errorCode, Exception e){
        this.code = errorCode.name();
        this.message = errorCode.getMessage(e.getMessage());
    }

    private ErrorResponseDto(Code errorCode, String message){
        this.code = errorCode.name();
        this.message = errorCode.getMessage(message);
    }

    public static ResponseEntity<ErrorResponseDto> of(Code errorCode){
        return ResponseEntity
                .status(errorCode.getCode())
                .body(new ErrorResponseDto(errorCode));
    }

    public static ResponseEntity<ErrorResponseDto> of(Code errorCode, Exception e){
        return ResponseEntity
                .status(errorCode.getCode())
                .body(new ErrorResponseDto(errorCode, e.getMessage()));
    }

    public static ResponseEntity<ErrorResponseDto> of(Code errorCode, String message,  Exception e){
        return ResponseEntity
                .status(errorCode.getCode())
                .body(new ErrorResponseDto(errorCode, message));
    }

}
