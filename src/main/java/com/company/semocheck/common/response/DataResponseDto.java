package com.company.semocheck.common.response;

import lombok.Getter;

@Getter
public class DataResponseDto<T> extends ResponseDto{

    private final T data;

    private DataResponseDto(T data){
        super(true, Code.OK.getCode(), Code.OK.getMessage());
        this.data = data;
    }

    private DataResponseDto(T data, String messsage){
        super(true, Code.OK.getCode(), messsage);
        this.data = data;
    }

    public static <T> DataResponseDto<T> of(T data){
        return new DataResponseDto<>(data);
    }

    public static <T> DataResponseDto<T> of(T data, String message){
        return new DataResponseDto<>(data, message);
    }


}
