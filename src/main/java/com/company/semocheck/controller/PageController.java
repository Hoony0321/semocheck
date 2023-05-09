package com.company.semocheck.controller;

import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "페이지", description = "페이지 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pages")
public class PageController {
    @ApiDocumentResponse
    @Operation(summary = "get home page API", description = "홈페이지를 조회합니다.(비로그인)\n\n")
    @GetMapping("/home")
    public DataResponseDto getHomePage(){
        return DataResponseDto.of("home", Code.SUCCESS);
    }
}
