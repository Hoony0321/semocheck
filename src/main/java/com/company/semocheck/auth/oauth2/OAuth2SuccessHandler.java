package com.company.semocheck.auth.oauth2;

import com.company.semocheck.auth.jwt.JwtProvider;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.Role;
import com.company.semocheck.domain.dto.MemberDto;
import com.company.semocheck.domain.dto.Token;
import com.company.semocheck.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final String REDIRECT_URL = "http://localhost:8080/api/test/auth/success";
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final MemberRepository memberRepository; //TODO : SERVICE로 바꾸기
    private final JwtProvider jwtProvider;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
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

        //JWT token 발급
        Token token = jwtProvider.generateToken(member);

        String targetUrl = UriComponentsBuilder //TODO : query 말고 header에 넣는 방법 찾아보기
                        .fromUriString(REDIRECT_URL)
                        .queryParam("access", token.getAccessToken())
                        .queryParam("refresh", token.getRefreshToken())
                        .build().toUriString();

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }
}
