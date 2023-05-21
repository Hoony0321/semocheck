package com.company.semocheck.domain.dto.checklist;

import com.company.semocheck.domain.dto.category.SubCategoryDetailDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class HomeChecklistDto {

    SubCategoryDetailDto category;
    List<ChecklistPostSimpleDto> checklists;

    public HomeChecklistDto(SubCategoryDetailDto category, List<ChecklistPostSimpleDto> checklists) {
        this.category = category;
        this.checklists = checklists;
    }
}
