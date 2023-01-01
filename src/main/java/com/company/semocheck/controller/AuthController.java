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
    private final MemberService memberService;

    private final JwtProvider jwtProvider;
    private final JwtUtils jwtUtils;


    @ApiDocumentResponse
    @Operation(summary = "Login API", description = "OAuth Token 받아서 JWT access & refresh Token을 반환합니다.")
    @GetMapping("/login")
    public DataResponseDto<Token> login(@RequestParam("token") String oAuthToken, @RequestParam("provider") String provider){
        RestTemplate restTemplate = new RestTemplate();
        Member member;
        URI requestUrl;
        ResponseEntity<Object> responseEntity;
        RequestEntity requestEntity = null;

        // TODO : apple sns login 추가하기
        switch (provider) {
            case "kakao" :
                requestUrl = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("kapi.kakao.com")
                    .path("/v2/user/me")
                    .encode().build().toUri();

                //OAuth token을 통해 리소스 서버로 사용자 정보 요청
                requestEntity = RequestEntity
                        .post(requestUrl)
                        .header("Authorization", "Bearer " + oAuthToken)
                        .body(null);
                break;
            case "google" :
                requestUrl = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("www.googleapis.com")
                    .path("/userinfo/v2/me")
                    .queryParam("access_token", oAuthToken)
                    .encode().build().toUri();
                break;
            default :
                throw new GeneralException(Code.BAD_REQUEST, "지원하지 않는 provider 입니다.");
        }

        try{
            if(requestEntity == null) responseEntity = restTemplate.getForEntity(requestUrl, Object.class);
            else responseEntity = restTemplate.postForEntity(requestUrl, requestEntity, Object.class);}
        catch (Exception e){
            throw new GeneralException(Code.BAD_REQUEST, "OAuth 인증 실패");}

        //받아온 사용자 정보를 동일한 form으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> oAuth2Info = objectMapper.convertValue(responseEntity.getBody(), Map.class);
        OAuth2Attributes attributes = OAuth2Attributes.of(provider, oAuth2Info);

        //해당 oauth_id로 가입된 계정 있는지 확인 -> 없으면 회원가입 진행
        Optional<Member> findOne = memberService.findByOAuthIdAndProvider(attributes.getId(), provider);
        if(findOne.isEmpty()){ //최초 로그인하는 경우 -> member 생성
            Long member_id = memberService.joinMember(attributes, provider);
            member = memberService.findById(member_id);
        }
        else{ //기존 계정 존재하는 경우 -> db에 있는 memeber 반환
            member = findOne.get();
        }

        //JWT token 발급
        Token jwtToken = jwtProvider.generateToken(member);

        return DataResponseDto.of(jwtToken);
    }

    @ApiDocumentResponse
    @Operation(summary = "Refresh Token API", description = "refresh token을 받아 새로운 access token을 반환합니다.")
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
