package com.company.semocheck.common;


import com.company.semocheck.auth.oauth2.OAuth2Attributes;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.request.category.CreateCategoryRequest;
import com.company.semocheck.domain.request.member.CreateMemberRequest;
import com.company.semocheck.service.CategoryService;
import com.company.semocheck.service.MemberCategoryService;
import com.company.semocheck.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
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
                .main("생활")
                .name("부동산").build();

        SubCategoryDto subCategoryDto2 = SubCategoryDto.builder()
                .main("생활")
                .name("결혼").build();

        SubCategoryDto subCategoryDto3 = SubCategoryDto.builder()
                .main("커리어")
                .name("면접").build();

        List<SubCategoryDto> subCategoryDtos = new ArrayList<>();
        subCategoryDtos.add(subCategoryDto1);
        subCategoryDtos.add(subCategoryDto2);
        subCategoryDtos.add(subCategoryDto3);

        return CreateMemberRequest.builder()
                .agreeNotify(testMemberAgreeNotify)
                .sex(testMemberSex)
                .age(testMemberAge)
                .categories(subCategoryDtos).build();
    }
}
