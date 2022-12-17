package com.company.semocheck.controller;

import com.company.semocheck.common.response.DataResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Tag(name = "테스트", description = "테스트용 API 모음입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {

    @Operation(summary = "test api 호출", description = "test api 호출 메서드입니다.")
    @GetMapping("/")
    public DataResponseDto<List> test(HttpServletRequest request){

        List testData = new ArrayList<>(Arrays.asList(1,2,3,4,5));

        return DataResponseDto.of(testData, "테스트 API입니다.");
    }


}
