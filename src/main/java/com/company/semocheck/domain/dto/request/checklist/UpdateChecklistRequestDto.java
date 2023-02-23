package com.company.semocheck.domain.dto.request.checklist;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UpdateChecklistRequestDto {

    private String title;
    private String brief;
    private String mainCategoryName;
    private String subCategoryName;
    private Boolean publish;
    private String fileId;
    private List<StepRequestDto> steps;

    @Builder
    public UpdateChecklistRequestDto(String title, String brief, String mainCategoryName, String subCategoryName, Boolean publish, String fileId, List<StepRequestDto> steps) {
        this.title = title;
        this.brief = brief;
        this.mainCategoryName = mainCategoryName;
        this.subCategoryName = subCategoryName;
        this.publish = publish;
        this.fileId = fileId;
        this.steps = steps;
    }
}
