package com.company.semocheck.service;

import com.company.semocheck.domain.category.MainCategory;
import com.company.semocheck.domain.request.category.CreateCategoryRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class CategoryServiceTest {

    @Autowired private MemberService memberService;
    @Autowired private CategoryService categoryService;

    @Test
    @Transactional
    public void 메인_카테고리_생성() throws Exception {
        //given
        String mainCategoryName1 = "main1";
        String mainCategoryName2 = "main2";
        String mainCategoryName3 = "main3";

        //when
        categoryService.createMainCategory(mainCategoryName1);
        categoryService.createMainCategory(mainCategoryName2);
        categoryService.createMainCategory(mainCategoryName3);

        //then
        assertThat(categoryService.findMainCategoryByName(mainCategoryName1).getName()).isEqualTo(mainCategoryName1);
        assertThat(categoryService.findMainCategoryByName(mainCategoryName2).getName()).isEqualTo(mainCategoryName2);
        assertThat(categoryService.findMainCategoryByName(mainCategoryName3).getName()).isEqualTo(mainCategoryName3);
    }

    @Test
    @Transactional
    public void 서브_카테고리_생성() throws Exception {
        //given
        String mainCategoryName = "main1";
        String subCategoryName1 = "sub1";
        String subCategoryName2 = "sub2";
        String subCategoryName3 = "sub3";
        categoryService.createMainCategory(mainCategoryName);

        //when
        categoryService.createSubCategory(new CreateCategoryRequest(mainCategoryName, subCategoryName1));
        categoryService.createSubCategory(new CreateCategoryRequest(mainCategoryName, subCategoryName2));
        categoryService.createSubCategory(new CreateCategoryRequest(mainCategoryName, subCategoryName3));

        //then
        MainCategory mainCategory = categoryService.findMainCategoryByName(mainCategoryName);
        assertThat(mainCategory.getSubCategoryList().size()).isEqualTo(3);
        assertThat(categoryService.findSubCategoryByName(mainCategoryName,subCategoryName1).getName()).isEqualTo(subCategoryName1);
        assertThat(categoryService.findSubCategoryByName(mainCategoryName,subCategoryName2).getName()).isEqualTo(subCategoryName2);
        assertThat(categoryService.findSubCategoryByName(mainCategoryName,subCategoryName3).getName()).isEqualTo(subCategoryName3);
    }

}
