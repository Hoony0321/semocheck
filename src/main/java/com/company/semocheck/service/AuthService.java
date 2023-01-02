package com.company.semocheck.service;

import com.company.semocheck.auth.jwt.JwtProvider;
import com.company.semocheck.auth.jwt.JwtUtils;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.RefreshToken;
import com.company.semocheck.domain.dto.Token;
import com.company.semocheck.exception.AuthException;
import com.company.semocheck.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberService memberService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final JwtUtils jwtUtils;

    @Transactional
    public Token reissueToken(Token token){ //TODO : refresh token은 기한이 조금 남았을 경우에만 update
        // Access token에서 Member 정보 가져오기
        Claims claims = jwtUtils.parseClaims(token.getAccessToken());
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));


        // Refresh repository에서 Member email 통해 refresh token entity 획득
        RefreshToken refreshToken = refreshTokenRepository.findByKey(member.getId())
                .orElseThrow(() -> new AuthException(Code.JWT_EXPIRED, "로그아웃 된 사용자입니다."));

        // Refresh token 일치하는지 확인
        if(!refreshToken.getValue().equals(token.getRefreshToken()))
            throw new AuthException(Code.BAD_REQUEST, "토큰의 유저 정보가 일치하지 않습니다.");

        // 새로운 토큰 생성
        Token newToken = jwtProvider.generateToken(member);

        // repository update
        refreshToken.updateValue(newToken.getRefreshToken());

        return newToken;
    }
}
