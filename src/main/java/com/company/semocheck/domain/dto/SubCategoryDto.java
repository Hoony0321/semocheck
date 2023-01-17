package com.company.semocheck.domain.dto;

import com.company.semocheck.domain.FileDetail;
import com.company.semocheck.domain.SubCategory;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubCategoryDto {
    private String name;
    private String main;

    private FileDetail fileDetail;

    @Builder
    public SubCategoryDto(String name, String main) {
        this.name = name;
        this.main = main;
    }

    static public SubCategoryDto createDto(SubCategory subCategory){
        SubCategoryDto dto = new SubCategoryDto();
        dto.name = subCategory.getName();
        dto.main = subCategory.getMainCategory().getName();
        dto.fileDetail = subCategory.getFileDetail();
        return dto;
    }
}
