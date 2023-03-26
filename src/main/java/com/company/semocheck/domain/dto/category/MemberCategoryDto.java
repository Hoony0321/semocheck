package com.company.semocheck.domain.dto.category;

import com.company.semocheck.domain.MemberCategory;
import com.company.semocheck.domain.SubCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberCategoryDto {

    private String name;
    private String main;

    static public MemberCategoryDto createDto(MemberCategory memberCategory){
        MemberCategoryDto dto = new MemberCategoryDto();
        SubCategory subCategory = memberCategory.getSubCategory();
        dto.main = subCategory.getMainCategory().getName();
        dto.name = subCategory.getName();
        return dto;
    }
}
