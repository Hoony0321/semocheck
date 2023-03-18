package com.company.semocheck.common.response;

import lombok.Getter;

@Getter
public class DataResponseDto<T> extends ResponseDto{

    private final T data;

    private DataResponseDto(T data){
        super(true, Code.SUCCESS.name(), Code.SUCCESS.getMessage());
        this.data = data;
    }

    private DataResponseDto(T data, Code code){
        super(true, code.name(), code.getMessage());
        this.data = data;
    }

    public static <T> DataResponseDto<T> of(T data){
        return new DataResponseDto<>(data);
    }

    public static <T> DataResponseDto<T> of(T data, Code code){
        return new DataResponseDto<>(data, code);
    }


}
