package com.company.semocheck.controller;

import com.company.semocheck.auth.oauth2.OAuth2Attributes;
import com.company.semocheck.common.TestCategoryUtils;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.request.member.CreateMemberRequest;
import com.company.semocheck.domain.response.LoginResponseDto;
import com.company.semocheck.service.CategoryService;
import com.company.semocheck.service.MemberService;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.bouncycastle.asn1.x500.style.RFC4519Style.member;


@SpringBootTest
class MemberControllerTest {

    @Autowired private MemberController memberController;
    @Autowired private MemberService memberService;
    @Autowired private CategoryService categoryService;
    @Autowired private TestCategoryUtils testCategoryUtils = new TestCategoryUtils();
    private final String provider = "google";
    private final String fcmToken = "fcmToken";
    private final String oAuthToken = "";

    @BeforeEach
    @Transactional
    public void before(){
        testCategoryUtils.initCategory();
    }

    @Test
    @Transactional
    public void 회원가입() throws Exception {
        //given
        List<SubCategoryDto> categories = new ArrayList<>();
        ;
        categories.add(SubCategoryDto.builder().main("main1").name("test1").build());
        categories.add(SubCategoryDto.builder().main("main2").name("test3").build());
        categories.add(SubCategoryDto.builder().main("main2").name("test4").build());

        CreateMemberRequest createMemberRequest = new CreateMemberRequest(false, false, 22, categories);

        //when
        memberController.joinMember(oAuthToken, provider, fcmToken, createMemberRequest);
        Map<String, Object> oAuth2Info = OAuth2Attributes.getOAuthInfo(oAuthToken, provider);
        OAuth2Attributes attributes = OAuth2Attributes.of(provider, oAuth2Info);
        Member member = memberService.findByOAuthIdAndProvider(attributes.getId(), provider);

        //then
        assertThat(member.getAge()).isEqualTo(22);
        assertThat(member.getSex()).isEqualTo(false);
        assertThat(member.getAgreeNotify()).isEqualTo(false);
        assertThat(member.getCategories().size()).isEqualTo(3);
    }

//    @Test
//    @Transactional
//    public void 회원정보_간단조회() throws Exception {
//        //given
//        Member member = memberService.findById(memberId);
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.addHeader("Authorization", "Bearer " + accessToken);
//
//        //when
//        MemberDto response = memberController.getMemberInfo(request).getData();
//
//        //then
//        assertThat(response.getId()).isEqualTo(member.getId());
//        assertThat(response.getName()).isEqualTo(member.getName());
//        assertThat(response.getPicture()).isEqualTo(member.getPicture());
//        //TODO : inProgressCount / completeCount / scrapCount / ownerCount / categories 검증 로직 생각
//    }
//
//    @Test
//    @Transactional
//    public void 회원정보_자세한조회() throws Exception {
//        //given
//        Member member = memberService.findById(memberId);
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.addHeader("Authorization", "Bearer " + accessToken);
//
//        //when
//        MemberDetailDto response = memberController.getMemberDetailInfo(request).getData();
//
//        //then
//        assertThat(response.getId()).isEqualTo(member.getId());
//        assertThat(response.getName()).isEqualTo(member.getName());
//        assertThat(response.getPicture()).isEqualTo(member.getPicture());
//        assertThat(response.getAge()).isEqualTo(member.getAge());
//        assertThat(response.getEmail()).isEqualTo(member.getEmail());
//        assertThat(response.getAgreeNotify()).isEqualTo(member.getAgreeNotify());
//        assertThat(response.getSex()).isEqualTo(member.getSex());
//        assertThat(response.getProvider()).isEqualTo(member.getProvider());
//    }
//
//    @Test
//    @Transactional
//    public void 회원정보_수정() throws Exception {
//        //given
//        Member member = memberService.findById(memberId);
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.addHeader("Authorization", "Bearer " + accessToken);
//
//        UpdateMemberRequest requestDto = UpdateMemberRequest.builder()
//                .name("modify name")
//                .agreeNotify(true)
//                .age(100)
//                .build();
//        //when
//        MemberDetailDto response = memberController.updateInfo(request, requestDto).getData();
//
//        //then
//        assertThat(response.getName()).isEqualTo("modify name");
//        assertThat(response.getAgreeNotify()).isEqualTo(true);
//        assertThat(response.getAge()).isEqualTo(100);
//        assertThat(response.getSex()).isEqualTo(true);
//    }
//
//    @Test
//    @Transactional
//    public void 회원정보_일부정보_수정() throws Exception {
//        //given
//        Member member = memberService.findById(memberId);
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.addHeader("Authorization", "Bearer " + accessToken);
//
//        UpdateMemberRequest requestDto = UpdateMemberRequest.builder()
//                .name("modify name")
//                .agreeNotify(true)
//                .build();
//        //when
//        MemberDetailDto response = memberController.updateInfo(request, requestDto).getData();
//
//        //then
//        assertThat(response.getName()).isEqualTo("modify name");
//        assertThat(response.getAgreeNotify()).isEqualTo(true);
//        assertThat(response.getSex()).isEqualTo(null);
//        assertThat(response.getAge()).isEqualTo(null); //입력하지  않으면 null로 입력됨.
//    }

}