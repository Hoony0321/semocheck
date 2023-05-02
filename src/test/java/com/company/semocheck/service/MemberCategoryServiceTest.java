package com.company.semocheck.service;

import com.company.semocheck.common.TestCategoryUtils;
import com.company.semocheck.common.TestMemberUtils;
import com.company.semocheck.domain.category.SubCategory;
import com.company.semocheck.domain.dto.category.MemberCategoryDto;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.member.MemberCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class MemberCategoryServiceTest {


    @Autowired private MemberService memberService;
    @Autowired private MemberCategoryService memberCategoryService;
    @Autowired private CategoryService categoryService;

    @Autowired private TestMemberUtils testMemberUtils;
    @Autowired private TestCategoryUtils testCategoryUtils;

    private Long memberId;
    private SubCategory subCategory1, subCategory2, subCategory3, subCategory4, subCategory5;
    @BeforeEach
    @Transactional
    public void before() {
        testCategoryUtils.initCategory();
        memberId = testMemberUtils.createMember();
        subCategory1 = categoryService.findSubCategoryByName("main1", "test1");
        subCategory2 = categoryService.findSubCategoryByName("main1", "test2");
        subCategory3 = categoryService.findSubCategoryByName("main2", "test3");
        subCategory4 = categoryService.findSubCategoryByName("main2", "test4");
        subCategory5 = categoryService.findSubCategoryByName("main3", "test5");
    }


    @Test
    @Transactional
    public void 회원카테고리_초기설정() throws Exception {
        //given
        Member member = memberService.findById(memberId);

        //when
        List<SubCategory> categories = new ArrayList<>();
        for (MemberCategory category : memberCategoryService.getCategories(member)) {
            categories.add(category.getSubCategory());
        }


        //then
        assertThat(categories.size()).isEqualTo(3);
        assertThat(categories.contains(subCategory1)).isEqualTo(true);
        assertThat(categories.contains(subCategory2)).isEqualTo(false);
        assertThat(categories.contains(subCategory3)).isEqualTo(true);
        assertThat(categories.contains(subCategory4)).isEqualTo(false);
        assertThat(categories.contains(subCategory5)).isEqualTo(true);
    }

    @Test
    @Transactional
    public void 회원카테고리_추가() throws Exception {
        //given
        Member member = memberService.findById(memberId);

        //when
        List<SubCategory> categories = new ArrayList<>();
        categories.add(subCategory2);
        categories.add(subCategory4);
        memberCategoryService.addMemberCategory(member, categories);

        //then
        categories = new ArrayList<>();
        for (MemberCategory category : memberCategoryService.getCategories(member)) {
            categories.add(category.getSubCategory());
        }

        assertThat(member.getCategories().size()).isEqualTo(5);
        assertThat(categories.contains(subCategory1)).isEqualTo(true);
        assertThat(categories.contains(subCategory2)).isEqualTo(true);
        assertThat(categories.contains(subCategory3)).isEqualTo(true);
        assertThat(categories.contains(subCategory4)).isEqualTo(true);
        assertThat(categories.contains(subCategory5)).isEqualTo(true);
    }

    @Test
    @Transactional
    public void 회원카테고리_수정() throws Exception {
        //given
        Member member = memberService.findById(memberId);

        //when
        List<SubCategory> categories = new ArrayList<>();
        categories.add(subCategory2);
        categories.add(subCategory4);
        memberCategoryService.updateMemberCategory(member, categories);

        //then
        categories = new ArrayList<>();
        for (MemberCategory category : memberCategoryService.getCategories(member)) {
            categories.add(category.getSubCategory());
        }

        assertThat(member.getCategories().size()).isEqualTo(2);
        assertThat(categories.contains(subCategory1)).isEqualTo(false);
        assertThat(categories.contains(subCategory2)).isEqualTo(true);
        assertThat(categories.contains(subCategory3)).isEqualTo(false);
        assertThat(categories.contains(subCategory4)).isEqualTo(true);
        assertThat(categories.contains(subCategory5)).isEqualTo(false);

    }

    @Test
    @Transactional
    public void 회원카테고리_삭제() throws Exception {
        //given
        Member member = memberService.findById(memberId);

        //when
        List<SubCategory> categories = new ArrayList<>();
        categories.add(subCategory1);
        categories.add(subCategory3);
        memberCategoryService.deleteMemberCategory(member, categories);

        //then
        categories = new ArrayList<>();
        for (MemberCategory category : memberCategoryService.getCategories(member)) {
            categories.add(category.getSubCategory());
        }

        assertThat(member.getCategories().size()).isEqualTo(1);
        assertThat(categories.contains(subCategory1)).isEqualTo(false);
        assertThat(categories.contains(subCategory3)).isEqualTo(false);
        assertThat(categories.contains(subCategory5)).isEqualTo(true);
    }


}
