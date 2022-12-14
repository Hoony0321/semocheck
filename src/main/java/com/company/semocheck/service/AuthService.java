package com.company.semocheck.service;

import com.company.semocheck.auth.jwt.JwtProvider;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.RefreshToken;
import com.company.semocheck.dto.Token;
import com.company.semocheck.exception.ApiException;
import com.company.semocheck.exception.ErrorCode;
import com.company.semocheck.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @Transactional
    public Token reissueToken(Token token){ //TODO : refresh token은 기한이 조금 남았을 경우에만 update
        // Access token에서 Member 정보 가져오기
        Authentication authentication = jwtProvider.getAuthentication(token.getAccessToken());
        String memberEmail = authentication.getName();

        // Refresh repository에서 Member email 통해 refresh token entity 획득
        RefreshToken refreshToken = refreshTokenRepository.findByKey(memberEmail)
                .orElseThrow(() -> new ApiException(ErrorCode.JWT_EXPIRED.getHttpStatus(), "로그아웃 된 사용자입니다."));

        // Refresh token 일치하는지 확인
        if(!refreshToken.getValue().equals(token.getRefreshToken()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "토큰의 유저 정보가 일치하지 않습니다.");

        Member member = memberService.findByEmail(memberEmail);

        // 새로운 토큰 생성
        Token newToken = jwtProvider.generateToken(member);

        // repository update
        refreshToken.updateValue(newToken.getRefreshToken());

        return newToken;
    }
}