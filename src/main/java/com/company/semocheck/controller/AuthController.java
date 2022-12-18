package com.company.semocheck.controller;

import com.company.semocheck.auth.jwt.JwtUtils;
import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.domain.dto.Token;
import com.company.semocheck.repository.MemberRepository;
import com.company.semocheck.service.AuthService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증", description = "인증 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @ApiDocumentResponse
    @Operation(summary = "Token generate API", description = "OAuth2 로그인 성공 후 Token을 생성해서 반환하는 API입니다.")
    @GetMapping("/token")
    public DataResponseDto<Token> generateToken(@RequestParam("access") String accessToken, @RequestParam("refresh") String refreshToken){
        Token token = new Token(accessToken, refreshToken);

        return DataResponseDto.of(token);
    }

    @ApiDocumentResponse
    @Operation(summary = "OAuth login test API", description = "OAuth2 JWT 토큰 체크하는 테스트 API입니다.")
    @GetMapping("/access")
    public DataResponseDto<String> oAuthSuccess(HttpServletRequest request){
        String accessToken = jwtUtils.getAccessToken(request);

        return DataResponseDto.of(accessToken, "JWT token access 성공");
    }

    @GetMapping("/refresh")
    public DataResponseDto<Token> refresh(HttpServletRequest request){
        String accessToken = jwtUtils.getAccessToken(request);
        String refreshToken = jwtUtils.getRefreshToken(request);

        Token token = Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();

        Token newToken = authService.reissueToken(token);

        return DataResponseDto.of(newToken, "JWT token access 성공");
    }

}
