package com.company.semocheck.service;

import com.company.semocheck.auth.jwt.JwtProvider;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.dto.Token;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class AuthServiceTest {

    @Autowired private JwtProvider jwtProvider;
    @Autowired private MemberService memberService;

    @Test
    @Transactional
    public void test_generate_token() throws Exception {
        //given
        final long memberId = 16;
        Member member = memberService.findById(memberId);

        //when
        Token token = jwtProvider.generateToken(member);

        //then
        System.out.println(token.getAccessToken());
    }

}