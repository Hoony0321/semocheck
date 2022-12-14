package com.company.semocheck.common;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@NoArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ApiResponse<Object> success(String message, Object data){
        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data).build();
    }
}
