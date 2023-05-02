package com.company.semocheck.service;

import com.company.semocheck.auth.oauth2.OAuth2Attributes;
import com.company.semocheck.common.TestCategoryUtils;
import com.company.semocheck.common.TestMemberUtils;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import com.company.semocheck.domain.request.member.CreateMemberRequest;
import com.company.semocheck.domain.request.member.UpdateMemberRequest;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
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
    @Autowired private MemberCategoryService memberCategoryService;


    @Autowired private TestMemberUtils testMemberUtils;
    @Autowired private TestCategoryUtils testCategoryUtils;


    private final int testMemberAge = 20;
    private final boolean testMemberSex = false;
    private final boolean testMemberAgreeNotify = true;
    private final String testMemberName = "testName";
    private final String testMemberEmail = "testEmail";
    private final String testMemberPicture = "testPicture";
    private final String testMemberProvider = "kakao";
    private final String testMemberOAuthId = "testOAuthId";
    private final String testMemberFcmToken = "testFcmToken";

    @BeforeEach
    @Transactional
    public void init(){
        testCategoryUtils.initCategory();
    }

    @Test
    @Transactional
    public void 회원생성_SUCCESS() throws Exception {
        //given
        OAuth2Attributes oAuth2Attributes = new OAuth2Attributes(testMemberOAuthId, testMemberName, testMemberEmail, testMemberPicture);
        String provider = testMemberProvider;
        String fcmToken = testMemberFcmToken;
        CreateMemberRequest request = createMemberRequest();

        //when
        Long memberId = memberService.createMember(oAuth2Attributes, provider, request, fcmToken);
        Member member = memberRepository.findById(memberId).get();
        memberCategoryService.initMemberCategory(member, request.getCategories());

        //then
        assertThat(member.getId()).isEqualTo(memberId);
        assertThat(member.getName()).isEqualTo(testMemberName);
        assertThat(member.getProvider()).isEqualTo(testMemberProvider);
        assertThat(member.getPicture()).isEqualTo(testMemberPicture);
        assertThat(member.getEmail()).isEqualTo(testMemberEmail);
        assertThat(member.getOAuthId()).isEqualTo(testMemberOAuthId);
        assertThat(member.getSex()).isEqualTo(testMemberSex);
        assertThat(member.getAge()).isEqualTo(testMemberAge);
        assertThat(member.getAgreeNotify()).isEqualTo(testMemberAgreeNotify);
    }

    @Test
    @Transactional
    public void 회원생성_EXISTED() throws Exception {
        //given
        testMemberUtils.createMember();

        //when


        //then
        GeneralException exception = assertThrows(GeneralException.class, () -> {
            testMemberUtils.createMember();
        });

        assertThat(exception.getErrorCode()).isEqualTo(Code.BAD_REQUEST);
        assertThat(exception.getMessage()).isEqualTo(ErrorMessages.EXISTED_MEMBER.getMessage());
    }

    @Test
    @Transactional
    public void 회원생성_INVALID_CATEGORY() throws Exception {
        //given
        OAuth2Attributes oAuth2Attributes = new OAuth2Attributes(testMemberOAuthId, testMemberName, testMemberEmail, testMemberPicture);
        String provider = testMemberProvider;
        String fcmToken = testMemberFcmToken;
        CreateMemberRequest request = createMemberRequest();

        //when
        request.getCategories().add(new SubCategoryDto("wrongSubName", "wrongMainName"));

        //then
        GeneralException exception = assertThrows(GeneralException.class, () -> {
            Long memberId = memberService.createMember(oAuth2Attributes, provider, request, fcmToken);
            Member member = memberService.findById(memberId);
            memberCategoryService.initMemberCategory(member, request.getCategories());
        });
        assertThat(exception.getErrorCode()).isEqualTo(Code.NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo(ErrorMessages.NOT_FOUND_CATEGORY.getMessage());
    }

    @Test
    @Transactional
    public void 회원생성_INVALID_PROVIDER() throws Exception {
        //given
        OAuth2Attributes oAuth2Attributes = new OAuth2Attributes(testMemberOAuthId, testMemberName, testMemberEmail, testMemberPicture);
        String provider = "wrongProvider";
        String fcmToken = testMemberFcmToken;
        CreateMemberRequest request = createMemberRequest();

        //when
        GeneralException exception = assertThrows(GeneralException.class, () -> {
            memberService.createMember(oAuth2Attributes, provider, request, fcmToken);
        });

        //then
        assertThat(exception.getErrorCode()).isEqualTo(Code.BAD_REQUEST);
        assertThat(exception.getMessage()).isEqualTo(ErrorMessages.INVALID_PROVIDER.getMessage());
    }

    @Test
    @Transactional
    public void 회원조회_SUCCESS() throws Exception {
        //given
        Long memberId = testMemberUtils.createMember();

        //when
        Member member = memberService.findById(memberId);

        //then
        assertThat(testMemberName).isEqualTo(member.getName());
    }

    @Test
    @Transactional
    public void 회원조회_NOT_FOUND() throws Exception {
        //given

        //when
        Long wrongMemberId = 1000000000l;

        //then
        GeneralException exception = assertThrows(GeneralException.class, () -> {
            memberService.findById(wrongMemberId);
        });
        assertThat(exception.getErrorCode()).isEqualTo(Code.NOT_FOUND);
    }

    @Test
    @Transactional
    public void 회원조회_WITH_OAUTHID_PROVIDER() throws Exception {
        //given
        Long memberId = testMemberUtils.createMember();

        //when
        Member member = memberService.findByOAuthIdAndProvider(testMemberOAuthId, testMemberProvider);

        //then
        assertThat(memberId).isEqualTo(member.getId());
        assertThat(testMemberName).isEqualTo(member.getName());
    }

    @Test
    @Transactional
    public void 회원조회_CHECK_WITH_OAUTHID_PROVIDER() throws Exception {
        //given
        Long memberId = testMemberUtils.createMember();

        //when
        Optional<Member> findOne1 = memberService.checkByOAuthIdAndProvider(testMemberOAuthId, testMemberProvider);
        Optional<Member> findOne2 = memberService.checkByOAuthIdAndProvider("wrongOAuthId", "wrongProvider");

        //then
        assertThat(findOne1.isPresent()).isEqualTo(true);
        assertThat(findOne2.isPresent()).isEqualTo(false);
    }

    @Test
    @Transactional
    public void 회원정보수정_TOTAL() throws Exception {
        //given
        Long memberId = testMemberUtils.createMember();
        Member member = memberRepository.findById(memberId).get();

        //when
        UpdateMemberRequest updateMemberRequest = UpdateMemberRequest.builder()
                .age(30)
                .agreeNotify(false)
                .name("modifiedName")
                .build();
        memberService.updateInfo(member, updateMemberRequest);

        //then
        assertThat(member.getAge()).isEqualTo(30);
        assertThat(member.getAgreeNotify()).isEqualTo(false);
        assertThat(member.getName()).isEqualTo("modifiedName");
    }

    @Test
    @Transactional
    public void 회원정보수정_일부() throws Exception {
        //given
        Long memberId = testMemberUtils.createMember();
        Member member = memberRepository.findById(memberId).get();
        String originName = member.getName();

        //when
        UpdateMemberRequest updateMemberRequest = UpdateMemberRequest.builder()
                .age(30)
                .build();
        memberService.updateInfo(member, updateMemberRequest);

        //then
        assertThat(member.getAge()).isEqualTo(30);
        assertThat(member.getName()).isEqualTo(originName);
    }


    private CreateMemberRequest createMemberRequest(){
        SubCategoryDto subCategoryDto1 = SubCategoryDto.builder()
                .main("main1")
                .name("test1").build();

        SubCategoryDto subCategoryDto2 = SubCategoryDto.builder()
                .main("main1")
                .name("test2").build();

        SubCategoryDto subCategoryDto3 = SubCategoryDto.builder()
                .main("main2")
                .name("test3").build();

        SubCategoryDto subCategoryDto4 = SubCategoryDto.builder()
                .main("main3")
                .name("test5").build();

        List<SubCategoryDto> subCategoryDtos = new ArrayList<>();
        subCategoryDtos.add(subCategoryDto1);
        subCategoryDtos.add(subCategoryDto2);
        subCategoryDtos.add(subCategoryDto3);
        subCategoryDtos.add(subCategoryDto4);

        return CreateMemberRequest.builder()
                .agreeNotify(testMemberAgreeNotify)
                .sex(testMemberSex)
                .age(testMemberAge)
                .categories(subCategoryDtos).build();
    }

}