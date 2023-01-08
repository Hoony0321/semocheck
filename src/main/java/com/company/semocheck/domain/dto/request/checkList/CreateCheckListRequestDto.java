package com.company.semocheck.domain.dto.request.checkList;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class CreateCheckListRequestDto {

    private String title;
    private String subTitle;
    private String brief;
    private String mainCategoryName;
    private String subCategoryName;
    private Integer ageGroup;
    private Boolean visibility;
    private List<StepRequestDto> steps;

    @Builder
    public CreateCheckListRequestDto(String title, String subTitle, String brief, String mainCategoryName, String subCategoryName,
                                     Integer ageGroup, Boolean visibility, List<StepRequestDto> steps) {
        this.title = title;
        this.subTitle = subTitle;
        this.brief = brief;
        this.mainCategoryName = mainCategoryName;
        this.subCategoryName = subCategoryName;
        this.ageGroup = ageGroup;
        this.visibility = visibility;
        this.steps = steps;
    }
}
