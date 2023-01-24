package com.company.semocheck.domain.dto.category;

import com.company.semocheck.domain.MainCategory;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MainCategoryDto {

    private String name;

    @Builder
    public MainCategoryDto(String name) {
        this.name = name;
    }

    public static MainCategoryDto createDto(MainCategory mainCategory){
        MainCategoryDto dto = new MainCategoryDto();
        dto.name = mainCategory.getName();
        return dto;
    }
}
