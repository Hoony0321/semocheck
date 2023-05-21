package com.company.semocheck.domain.dto.category;

import com.company.semocheck.domain.category.SubCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubCategoryDetailDto {
    private String name;
    private String main;
    private String description;

    static public SubCategoryDetailDto createDto(SubCategory subCategory) {
        SubCategoryDetailDto dto = new SubCategoryDetailDto();
        dto.name = subCategory.getName();
        dto.main = subCategory.getMainCategory().getName();
        dto.description = subCategory.getDescription();
        return dto;
    }
}
