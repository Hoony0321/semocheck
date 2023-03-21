package com.company.semocheck.common;


import com.company.semocheck.domain.dto.category.SubCategoryDto;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class TestUtils {

    public List<SubCategoryDto> getTestCategoryDtos(){
        SubCategoryDto subCategoryDto1 = SubCategoryDto.builder()
                .main("생활")
                .name("부동산").build();

        SubCategoryDto subCategoryDto2 = SubCategoryDto.builder()
                .main("생활")
                .name("인테리어").build();

        List<SubCategoryDto> subCategoryDtos = new ArrayList<>();
        subCategoryDtos.add(subCategoryDto1);
        subCategoryDtos.add(subCategoryDto2);

        return subCategoryDtos;
    }
}
