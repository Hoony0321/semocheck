package com.company.semocheck.domain.dto.request.checkList;

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
}
