package com.company.semocheck.common;


import com.company.semocheck.auth.oauth2.OAuth2Attributes;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.request.member.CreateMemberRequest;
import com.company.semocheck.service.MemberCategoryService;
import com.company.semocheck.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestMemberUtils {

    @Autowired private MemberService memberService;
    @Autowired private MemberCategoryService memberCategoryService;

    private final int testMemberAge = 20;
    private final boolean testMemberSex = false;
    private final boolean testMemberAgreeNotify = true;
    private final String testMemberName = "testName";
    private final String testMemberEmail = "testEmail";
    private final String testMemberPicture = "testPicture";
    private final String testMemberProvider = "kakao";
    private final String testOAuthId = "testOAuthId";
    private final String testFcmToken = "testFcmToken";

    public Long createMember(){
        OAuth2Attributes oAuth2Attributes = new OAuth2Attributes( testOAuthId, testMemberName, testMemberEmail, testMemberPicture);
        String provider = testMemberProvider;
        String fcmToken = testFcmToken;
        CreateMemberRequest request = createMemberRequest();

        Long memberId = memberService.createMember(oAuth2Attributes, provider, request, fcmToken);
        Member member = memberService.findById(memberId);

        memberCategoryService.initMemberCategory(member, request.getCategories());
        return memberId;
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
                .name("test1").build();

        SubCategoryDto subCategoryDto4 = SubCategoryDto.builder()
                .main("main3")
                .name("test1").build();

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
