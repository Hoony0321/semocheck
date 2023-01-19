package com.company.semocheck.domain.dto.request.checkList;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UpdateCheckListRequestDto {

    private String title;
    private String brief;
    private String mainCategoryName;
    private String subCategoryName;
    private Boolean visibility;
    private List<StepRequestDto> steps;

    @Builder
    public UpdateCheckListRequestDto(String title, String brief, String mainCategoryName, String subCategoryName, Boolean visibility, List<StepRequestDto> steps) {
        this.title = title;
        this.brief = brief;
        this.mainCategoryName = mainCategoryName;
        this.subCategoryName = subCategoryName;
        this.visibility = visibility;
        this.steps = steps;
    }
}
