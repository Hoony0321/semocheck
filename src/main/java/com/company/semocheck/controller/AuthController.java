package com.company.semocheck.controller;

import com.company.semocheck.auth.jwt.JwtProvider;
import com.company.semocheck.auth.jwt.JwtUtils;
import com.company.semocheck.auth.oauth2.OAuth2Attributes;
import com.company.semocheck.common.response.*;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.RefreshToken;
import com.company.semocheck.domain.dto.Token;
import com.company.semocheck.domain.dto.response.LoginResponseDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.AuthService;
import com.company.semocheck.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Tag(name = "인증", description = "인증 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;

    private final JwtProvider jwtProvider;
    private final JwtUtils jwtUtils;


    @ApiDocumentResponse
    @Operation(summary = "Login API", description = "OAuth Token 받아서 JWT access & refresh Token을 반환합니다.")
    @GetMapping("/login")
    public DataResponseDto<LoginResponseDto> login(@RequestParam("oAuthToken") String oAuthToken, @RequestParam("provider") String provider){
        Member member;
        Map<String, Object> oAuth2Info = OAuth2Attributes.getOAuthInfo(oAuthToken, provider);
        OAuth2Attributes attributes = OAuth2Attributes.of(provider, oAuth2Info);

        //해당 oauth_id로 가입된 계정 있는지 확인 -> 없으면 회원가입 진행
        Optional<Member> findOne = memberService.checkByOAuthIdAndProvider(attributes.getId(), provider);
        if(findOne.isEmpty()){ //최초 로그인하는 경우 -> error response
            throw new GeneralException(Code.NOT_FOUND, "해당 계정의 회원은 존재하지 않습니다.");
        }
        else{ //기존 계정 존재하는 경우 -> db에 있는 memeber 반환
            member = findOne.get();
        }

        //JWT token 발급
        Token jwtToken = jwtProvider.generateToken(member);

        LoginResponseDto response = LoginResponseDto.builder()
                .accessToken(jwtToken.getAccessToken())
                .refreshToken(jwtToken.getRefreshToken())
                .id(member.getId()).build();
        return DataResponseDto.of(response);
    }

    @ApiDocumentResponse
    @Operation(summary = "Refresh Token API", description = "refresh token / access token을 받아 새로운 access token을 반환합니다.")
    @GetMapping("/refresh")
    public DataResponseDto<Token> refresh(@RequestParam String accessToken, @RequestParam String refreshToken){
        Token token = Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
        Token newToken = authService.reissueToken(token);

        return DataResponseDto.of(newToken, "JWT token 재발급 성공");
    }

}
