package com.company.semocheck.controller;

import com.company.semocheck.auth.jwt.JwtProvider;
import com.company.semocheck.auth.jwt.JwtUtils;
import com.company.semocheck.auth.oauth2.OAuth2Attributes;
import com.company.semocheck.common.response.*;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.dto.Token;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.AuthService;
import com.company.semocheck.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;

@Tag(name = "인증", description = "인증 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final MemberRepository memberRepository;

    private final JwtProvider jwtProvider;
    private final JwtUtils jwtUtils;


    @ApiDocumentResponse
    @Operation(summary = "Login API", description = "OAuth Token 받아서 JWT Token을 반환하는 API입니다.")
    @GetMapping("/login")
    public DataResponseDto<Token> login(@RequestParam("token") String token, @RequestParam("provider") String provider){
        RestTemplate restTemplate = new RestTemplate();

        URI requestUrl = switch (provider) {
            case "kakao" -> UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("kapi.kakao.com")
                    .path("/v2/user/me")
                    .encode().build().toUri();
            case "google" -> UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("kapi.kakao.com")
                    .path("/v2/user/me")
                    .encode().build().toUri();
            default -> throw new GeneralException(Code.BAD_REQUEST, "잘못된 provider 요청");
        };

        RequestEntity<Object> requestEntity = RequestEntity
                .post(requestUrl)
                .header("Authorization", "Bearer " + token)
                .body(null);


        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(requestUrl, requestEntity, Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> oAuth2Info = objectMapper.convertValue(responseEntity.getBody(), Map.class);
        OAuth2Attributes attributes = OAuth2Attributes.of(provider, oAuth2Info);

        //TODO : refactoring & 최초 로그인 시 어떻게 할지 결정
        Optional<Member> findOne = memberRepository.findByEmail(attributes.getEmail());
        Member member = null;
        if(findOne.isEmpty()){ //최초 로그인 시
            member = Member.builder()
                    .email(attributes.getEmail())
                    .name(attributes.getName())
                    .picture(attributes.getPicture())
                    .role(Role.USER)
                    .build();
            memberRepository.save(member);
        }
        else{ member = findOne.get(); }

        //JWT token 발급
        Token jwtToken = jwtProvider.generateToken(member);


        return DataResponseDto.of(jwtToken);
    }

    @ApiDocumentResponse
    @Operation(summary = "Token generate API", description = "OAuth2 로그인 성공 후 Token을 생성해서 반환하는 API입니다.")
    @GetMapping("/token")
    public DataResponseDto<Token> generateToken(@RequestParam("access") String accessToken, @RequestParam("refresh") String refreshToken){
        Token token = new Token(accessToken, refreshToken);

        return DataResponseDto.of(token);
    }

    @ApiDocumentResponse
    @Operation(summary = "Token generate API", description = "OAuth2 로그인 성공 후 Token을 생성해서 반환하는 API입니다.")
    @GetMapping("/token/fail")
    public DataResponseDto<Token> failOAuth(HttpServletRequest request){
        Enumeration<String> names = request.getHeaderNames();
        throw new GeneralException(Code.INTERNAL_ERROR, "OAuth 인증 실패");
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
