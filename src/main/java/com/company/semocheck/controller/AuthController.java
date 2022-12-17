package com.company.semocheck.controller;

import com.company.semocheck.auth.jwt.JwtUtils;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.dto.Token;
import com.company.semocheck.repository.MemberRepository;
import com.company.semocheck.service.AuthService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final MemberRepository memberRepository;

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    //OAuth Login 성공 시
    @GetMapping("/access")
    public DataResponseDto<String> oAuthSuccess(HttpServletRequest request){
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims1 = jwtUtils.parseClaims(accessToken);

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
