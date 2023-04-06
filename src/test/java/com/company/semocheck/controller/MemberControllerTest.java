package com.company.semocheck.controller;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.dto.member.MemberDetailDto;
import com.company.semocheck.domain.dto.member.MemberDto;
import com.company.semocheck.domain.request.member.UpdateMemberRequest;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.MemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberControllerTest {

    @Autowired private MemberController memberController;
    @Autowired private MemberService memberService;


    private final Long memberId = 2l;
    private final String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdHkiOiJST0xFX1VTRVIiLCJvQXV0aElkIjoiMjU3NTkyNjA2MSIsInByb3ZpZGVyIjoia2FrYW8iLCJleHAiOjE2NzkyMTAwMzF9.1JM5gKOpbKT2ymUcK5gYUt2MRETNC6i-dtAINiUJQUo";

    @Test
    @Transactional
    public void 회원정보_간단조회() throws Exception {
        //given
        Member member = memberService.findById(memberId);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + accessToken);

        //when
        MemberDto response = memberController.getMemberInfo(request).getData();

        //then
        assertThat(response.getId()).isEqualTo(member.getId());
        assertThat(response.getName()).isEqualTo(member.getName());
        assertThat(response.getPicture()).isEqualTo(member.getPicture());
        //TODO : inProgressCount / completeCount / scrapCount / ownerCount / categories 검증 로직 생각
    }

    @Test
    @Transactional
    public void 회원정보_자세한조회() throws Exception {
        //given
        Member member = memberService.findById(memberId);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + accessToken);

        //when
        MemberDetailDto response = memberController.getMemberDetailInfo(request).getData();

        //then
        assertThat(response.getId()).isEqualTo(member.getId());
        assertThat(response.getName()).isEqualTo(member.getName());
        assertThat(response.getPicture()).isEqualTo(member.getPicture());
        assertThat(response.getAge()).isEqualTo(member.getAge());
        assertThat(response.getEmail()).isEqualTo(member.getEmail());
        assertThat(response.getAgreeNotify()).isEqualTo(member.getAgreeNotify());
        assertThat(response.getSex()).isEqualTo(member.getSex());
        assertThat(response.getProvider()).isEqualTo(member.getProvider());
    }

    @Test
    @Transactional
    public void 회원정보_수정() throws Exception {
        //given
        Member member = memberService.findById(memberId);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + accessToken);

        UpdateMemberRequest requestDto = UpdateMemberRequest.builder()
                .name("modify name")
                .agreeNotify(true)
                .age(100)
                .build();
        //when
        MemberDetailDto response = memberController.updateInfo(request, requestDto).getData();

        //then
        assertThat(response.getName()).isEqualTo("modify name");
        assertThat(response.getAgreeNotify()).isEqualTo(true);
        assertThat(response.getAge()).isEqualTo(100);
        assertThat(response.getSex()).isEqualTo(true);
    }

    @Test
    @Transactional
    public void 회원정보_일부정보_수정() throws Exception {
        //given
        Member member = memberService.findById(memberId);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + accessToken);

        UpdateMemberRequest requestDto = UpdateMemberRequest.builder()
                .name("modify name")
                .agreeNotify(true)
                .build();
        //when
        MemberDetailDto response = memberController.updateInfo(request, requestDto).getData();

        //then
        assertThat(response.getName()).isEqualTo("modify name");
        assertThat(response.getAgreeNotify()).isEqualTo(true);
        assertThat(response.getSex()).isEqualTo(null);
        assertThat(response.getAge()).isEqualTo(null); //입력하지  않으면 null로 입력됨.
    }

}