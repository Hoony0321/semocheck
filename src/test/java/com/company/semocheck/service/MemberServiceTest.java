package com.company.semocheck.service;

import com.company.semocheck.auth.oauth2.OAuth2Attributes;
import com.company.semocheck.common.TestUtils;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.SubCategory;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import com.company.semocheck.domain.request.member.CreateMemberRequest;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired private MemberRepository memberRepository;
    @Autowired private MemberService memberService;
    private TestUtils testUtils = new TestUtils();

    @Test
    public void test_findById() throws Exception {
        //given
        Long memberId = createMember("1");

        //when
        Member member = memberService.findById(memberId);

        //then
        assertThat("testName1").isEqualTo(member.getName());
    }

    @Test
    public void test_findById_notFoundError() throws Exception {
        //given
        //when
        //then
        GeneralException exception = assertThrows(GeneralException.class, () -> {
            memberService.findById(10000l);
        });
        assertThat(exception.getErrorCode()).isEqualTo(Code.NOT_FOUND);
    }

    @Test
    public void test_findByOAuthIdAndProvider() throws Exception {
        //given
        Long memberId = createMember("1");

        //when
        Member member = memberService.findByOAuthIdAndProvider("testOAuthId1", "kakao");

        //then
        assertThat(memberId).isEqualTo(member.getId());
        assertThat("testName1").isEqualTo(member.getName());
    }

    @Test
    public void test_checkByOAuthIdAndProvider() throws Exception {
        //given
        Long memberId1 = createMember("1");

        //when
        Optional<Member> findOne1 = memberService.checkByOAuthIdAndProvider("testOAuthId1", "kakao");
        Optional<Member> findOne2 = memberService.checkByOAuthIdAndProvider("testOAuthId2", "kakao");

        //then
        assertThat(findOne1.isPresent()).isEqualTo(true);
        assertThat(findOne2.isPresent()).isEqualTo(false);
    }

    @Test
    @Transactional
    public void test_createMember() throws Exception {
        //given
        OAuth2Attributes oAuth2Attributes = new OAuth2Attributes("testOAuthId", "testName", "testEmail", "testPicture");
        String provider = "kakao";
        String fcmToken = "test fcmToken";
        CreateMemberRequest request = createRequest();

        //when
        Long memberId = memberService.createMember(oAuth2Attributes, provider, request, fcmToken);
        Member member = memberRepository.findById(memberId).get();

        //then
        assertThat(member.getId()).isEqualTo(memberId);
        assertThat(member.getName()).isEqualTo("testName");
        assertThat(member.getProvider()).isEqualTo("kakao");
        assertThat(member.getPicture()).isEqualTo("testPicture");
        assertThat(member.getEmail()).isEqualTo("testEmail");
        assertThat(member.getOAuthId()).isEqualTo("testOAuthId");
        assertThat(member.getSex()).isEqualTo(true);
        assertThat(member.getAge()).isEqualTo(20);
        assertThat(member.getAgreeNotify()).isEqualTo(true);
        assertThat(member.getCategories().size()).isEqualTo(2);
    }

    @Test
    @Transactional
    public void test_createMember_notFoundCategory() throws Exception {
        //given
        OAuth2Attributes oAuth2Attributes = new OAuth2Attributes("testOAuthId", "testName", "testEmail", "testPicture");
        String provider = "kakao";
        String fcmToken = "test fcmToken";
        CreateMemberRequest request = createRequest();

        //when
        request.getCategories().add(new SubCategoryDto("testName", "testMain"));

        //then
        GeneralException exception = assertThrows(GeneralException.class, () -> {
            memberService.createMember(oAuth2Attributes, provider, request, fcmToken);
        });
        assertThat(exception.getErrorCode()).isEqualTo(Code.NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo(ErrorMessages.NOT_FOUND_CATEGORY.getMessage());
    }

    @Test
    @Transactional
    public void test_createMember_invalidProvider() throws Exception {
        //given
        OAuth2Attributes oAuth2Attributes = new OAuth2Attributes("testOAuthId", "testName", "testEmail", "testPicture");
        String provider = "invalid provider";
        String fcmToken = "test fcmToken";
        CreateMemberRequest request = createRequest();

        //when
        GeneralException exception = assertThrows(GeneralException.class, () -> {
            memberService.createMember(oAuth2Attributes, provider, request, fcmToken);
        });

        //then
        assertThat(exception.getErrorCode()).isEqualTo(Code.BAD_REQUEST);
        assertThat(exception.getMessage()).isEqualTo(ErrorMessages.INVALID_PROVIDER.getMessage());
    }

    private Long createMember(String number){
        OAuth2Attributes oAuth2Attributes = new OAuth2Attributes("testOAuthId" + number, "testName" + number, "testEmail" + number, "testPicture" + number);
        String provider = "kakao";
        String fcmToken = "test fcmToken";
        CreateMemberRequest request = createRequest();

        return memberService.createMember(oAuth2Attributes, provider, request, fcmToken);
    }

    private CreateMemberRequest createRequest(){
        List<SubCategoryDto> testCategoryDtos = testUtils.getTestCategoryDtos();
        return CreateMemberRequest.builder()
                .agreeNotify(true)
                .sex(true)
                .age(20)
                .categories(testCategoryDtos).build();
    }

}