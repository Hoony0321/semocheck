package com.company.semocheck.controller;

import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ErrorResponseDto;
import com.company.semocheck.domain.dto.TestDto;
import com.company.semocheck.domain.dto.Token;
import com.company.semocheck.exception.GeneralException;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Tag(name = "테스트", description = "테스트용 API 모음입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {

    //OAuth Login Redirection Test
    @Operation(summary = "OAuth login success API", description = "OAuth2 Login 성공 시 토큰 체크하는 테스트 API입니다.")
    @GetMapping("/auth/success")
    public DataResponseDto<Token> oAuthSuccess(@RequestParam("access") String accessToken, @RequestParam("refresh") String refreshToken){

        Token token = new Token(accessToken, refreshToken);

        return DataResponseDto.of(token, "login 성공");
    }

    @Operation(summary = "test api 호출1", description = "test api 호출 메서드입니다.")
    @GetMapping("/1")
    public DataResponseDto<List> test1(HttpServletRequest request){

        List testData = new ArrayList<>(Arrays.asList(1,2,3,4,5));

        return DataResponseDto.of(testData, "테스트 API입니다.");
    }

    @Operation(summary = "test api 호출 2", description = "test api 호출 메서드입니다.")
    @ApiDocumentResponse
    @GetMapping("/2")
    public DataResponseDto<TestDto> test2(HttpServletRequest request){

        TestDto data = new TestDto("test", 5, 5);

        double random = Math.random();

        if(random > 0.5) throw new GeneralException(Code.INTERNAL_ERROR, "데이터 전송 실패");

        return DataResponseDto.of(data, "테스트 API입니다.");
    }




}
