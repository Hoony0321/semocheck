package com.company.semocheck.domain.dto.category;

import com.company.semocheck.domain.MemberCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberCategoryDto {

    private Long id;
    private SubCategoryDto categoryDto;

    static public MemberCategoryDto createDto(MemberCategory memberCategory){
        MemberCategoryDto dto = new MemberCategoryDto();
        dto.id = memberCategory.getId();
        dto.categoryDto = SubCategoryDto.createDto(memberCategory.getSubCategory());
        return dto;
    }
}
