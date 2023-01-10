package com.company.semocheck.auth.jwt;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.RefreshToken;
import com.company.semocheck.domain.dto.Token;
import com.company.semocheck.exception.FilterException;
import com.company.semocheck.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${spring.jwt.secret-key}")
    private String SECRET_KEY;
    private static final String AUTHORITIES_KEY = "authority";
    private static final Long ACCESS_TOKEN_VALID_TIME = 24 * 60 * 60 * 1000L; // 24hours -> 추후에 10min으로 변경할 것
    private static final Long REFRESH_TOKEN_VALID_TIME = 24 * 60 * 60 * 1000L; // 24hours

    private final RefreshTokenRepository refreshTokenRepository;

    private Key key;

    // 의존성 주입 후, 초기화를 수행
    // 객체 초기화, secretKey Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Base64로 인코딩
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    // JWT 생성
    public Token generateToken(Member member){

        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpireTime = new Date(now + ACCESS_TOKEN_VALID_TIME);
        String accessToken = Jwts.builder()
                .claim(AUTHORITIES_KEY, member.getRoleKey())     // payload : "auth" : "ROLE_USER" or "ROLE_AUTH"
                .claim("oAuthId", member.getOAuthId()) // payload : "oAuthId"
                .claim("provider", member.getProvider()) // payload : "provider"
                .setExpiration(accessTokenExpireTime)    // payload : "exp" : 1516239022(에시)
                .signWith(key, SignatureAlgorithm.HS256) // header "alg" : "HS256"
                .compact();

        // Refresh Token 생성
        Date refreshTokenExpireTime = new Date(now + REFRESH_TOKEN_VALID_TIME);
        String refreshToken = Jwts.builder()
                .setSubject("refresh_token")
                .setExpiration(refreshTokenExpireTime)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token Repository에 저장
        refreshTokenRepository.save(RefreshToken.builder()
                        .key(member.getId())
                        .value(refreshToken)
                        .build());

        return new Token(accessToken, refreshToken);
    }

    public Authentication getAuthentication(String accessToken){

        //토큰 복호화
        Claims claims = parseClaims(accessToken);

        if(claims.get(AUTHORITIES_KEY) == null){
            throw new FilterException(Code.JWT_INVALID);
        }

        //클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.get("oAuthId").toString(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        }
        catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            //log.info("잘못된 JWT 서명입니다.");
            throw new FilterException(Code.JWT_INVALID_SIGN);
        }
        catch (ExpiredJwtException e){
            //log.info("만료된 JWT 토큰입니다.");
            throw new FilterException(Code.JWT_EXPIRED);
        }
        catch (UnsupportedJwtException e){
            //log.info("지원되지 않는 JWT 토큰입니다.");
            throw new FilterException(Code.JWT_UNSUPPORTED);
        }
        catch (IllegalArgumentException e){
            //log.info("올바른 JWT 토큰이 아닙니다.");
            throw new FilterException(Code.JWT_INVALID);
        }
        catch (Exception e){
            throw new FilterException(Code.JWT_PROCESS_ERROR);}
    }

    private Claims parseClaims(String accessToken){
        try{
            return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(accessToken).getBody();
        }
        catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }
}
