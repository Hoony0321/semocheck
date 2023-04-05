package com.company.semocheck.domain.request.checklist;


import com.company.semocheck.common.response.Code;
import com.company.semocheck.exception.GeneralException;
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

    //image
    private String imageId;
    private String defaultImageId;
    private String colorCode;

    @Builder
    public CreateChecklistRequest(String title, String brief, String mainCategoryName, String subCategoryName, Boolean publish, List<StepRequestDto> steps, String imageId, String defaultImageId, String colorCode) {
        this.title = title;
        this.brief = brief;
        this.mainCategoryName = mainCategoryName;
        this.subCategoryName = subCategoryName;
        this.publish = publish;
        this.steps = steps;
        this.imageId = imageId;
        this.defaultImageId = defaultImageId;
        this.colorCode = colorCode;
    }

    public void validate() {
        if(title == null || title.equals("")) throw new GeneralException(Code.ILLEGAL_ARGUMENT);
        if(mainCategoryName == null || mainCategoryName.equals("")) throw new GeneralException(Code.ILLEGAL_ARGUMENT);
        if(subCategoryName == null || subCategoryName.equals("")) throw new GeneralException(Code.ILLEGAL_ARGUMENT);
    }
}
