package com.company.semocheck.auth.oauth2;

import com.company.semocheck.auth.jwt.JwtProvider;
import com.company.semocheck.domain.dto.Token;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oAuthAuthentication = (OAuth2AuthenticationToken) authentication;

        //Member 조회
        String oAuthId = (oAuthAuthentication.getPrincipal().getAttributes().get("id")).toString();
        String provider = oAuthAuthentication.getAuthorizedClientRegistrationId();
        Member member = memberService.findByOAuthIdAndProvider(oAuthId, provider);

        //JWT token 발급
        Token jwtToken = jwtProvider.generateToken(member);
        log.info("access token : " + jwtToken.getAccessToken());

        //set response
        response.setHeader("Authorization", "Bearer " + jwtToken.getAccessToken());
        response.sendRedirect("/admin");
    }
}
