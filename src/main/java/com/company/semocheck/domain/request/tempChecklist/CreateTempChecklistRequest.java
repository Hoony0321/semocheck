package com.company.semocheck.domain.request.tempChecklist;

import com.company.semocheck.domain.request.checklist.StepRequestDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateTempChecklistRequest {
    private String title;
    private String brief;
    private String mainCategoryName;
    private String subCategoryName;
    private Boolean publish;
    private List<StepRequestDto> steps;
    private Integer defaultImage;
    private Integer temporary;

    //image
    private String imageId;
    private String defaultImageId;
    private String colorCode;

    @Builder
    public CreateTempChecklistRequest(String title, String brief, String mainCategoryName, String subCategoryName, Boolean publish, List<StepRequestDto> steps, Integer defaultImage, Integer temporary, String imageId, String defaultImageId, String colorCode) {
        this.title = title;
        this.brief = brief;
        this.mainCategoryName = mainCategoryName;
        this.subCategoryName = subCategoryName;
        this.publish = publish;
        this.steps = steps;
        this.defaultImage = defaultImage;
        this.temporary = temporary;
        this.imageId = imageId;
        this.defaultImageId = defaultImageId;
        this.colorCode = colorCode;
    }
}
