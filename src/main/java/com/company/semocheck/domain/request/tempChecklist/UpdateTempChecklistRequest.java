package com.company.semocheck.domain.request.tempChecklist;

import com.company.semocheck.domain.request.checklist.StepRequestDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UpdateTempChecklistRequest {
    private String title;
    private String brief;
    private String mainCategoryName;
    private String subCategoryName;
    private Boolean publish;
    private String imageId;
    private String defaultImageId;
    private String colorCode;
    private Integer temporary;
    private List<StepRequestDto> steps;
}
