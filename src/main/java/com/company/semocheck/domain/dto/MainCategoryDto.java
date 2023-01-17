package com.company.semocheck.domain.dto;

import com.company.semocheck.domain.FileDetail;
import com.company.semocheck.domain.SubCategory;
import com.company.semocheck.domain.MainCategory;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MainCategoryDto {

    private String name;
    private FileDto fileDto;

    @Builder
    public MainCategoryDto(String name) {
        this.name = name;
    }

    public static MainCategoryDto createDto(MainCategory mainCategory){
        MainCategoryDto dto = new MainCategoryDto();
        dto.name = mainCategory.getName();
        dto.fileDto = FileDto.createDto(mainCategory.getFileDetail());
        return dto;
    }
}
