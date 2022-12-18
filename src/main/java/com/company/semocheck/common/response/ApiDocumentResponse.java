package com.company.semocheck.common.response;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;



@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ApiResponses(value = {

        @ApiResponse(responseCode = "200", description = "API 호출 성공"),

        @ApiResponse(
                responseCode = "400",
                description = "잘못된 API 요청",
                content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),

        @ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),

        @ApiResponse(
                responseCode = "403",
                description = "권한 없음",
                content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),

        @ApiResponse(
                responseCode = "404",
                description = "존재하지 않은 리소스 요청",
                content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),

        @ApiResponse(
                responseCode = "500",
                description = "서버 오류",
                content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),

})
public @interface ApiDocumentResponse {
}

