package com.company.semocheck.domain.request.checklist;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class CreateChecklistRequest {

    private String title;
    private String brief;
    private String mainCategoryName;
    private String subCategoryName;
    private Boolean publish;
    private List<StepRequestDto> steps;
    private Integer defaultImage;

    private Integer temporary;
    private String fileId;

    @Builder
    public CreateChecklistRequest(String title, String brief, String mainCategoryName, String subCategoryName, Boolean publish, List<StepRequestDto> steps, Integer defaultImage, Integer temporary, String fileId) {
        this.title = title;
        this.brief = brief;
        this.mainCategoryName = mainCategoryName;
        this.subCategoryName = subCategoryName;
        this.publish = publish;
        this.steps = steps;
        this.defaultImage = defaultImage;
        this.temporary = temporary;
        this.fileId = fileId;
    }
}
