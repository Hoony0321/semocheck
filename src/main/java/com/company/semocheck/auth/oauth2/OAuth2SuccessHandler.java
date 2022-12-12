package com.company.semocheck.auth.oauth2;

import com.company.semocheck.auth.jwt.JwtProvider;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.Role;
import com.company.semocheck.dto.MemberDto;
import com.company.semocheck.dto.Token;
import com.company.semocheck.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository; //TODO : SERVICE로 바꾸기


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        MemberDto memberDto = MemberDto.createDto(oAuth2User);

        //TODO : refactoring & 최초 로그인 시 어떻게 할지 결정
        Optional<Member> findOne = memberRepository.findByEmail(memberDto.getEmail());
        Member member = null;
        if(findOne.isEmpty()){ //최초 로그인 시
            member = Member.builder()
                    .email(memberDto.getEmail())
                    .name(memberDto.getName())
                    .picture(memberDto.getPicture())
                    .role(Role.USER)
                    .build();
            memberRepository.save(member);
        }
        else{ member = findOne.get(); }

        String targetUrl = UriComponentsBuilder
                        .fromUriString("/api/auth/success")
                        .queryParam("email", member.getEmail())
                        .build().toUriString();

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }
}
