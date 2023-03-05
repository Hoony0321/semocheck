package com.company.semocheck.domain.request.checklist;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class CreateChecklistRequestDto {

    private String title;
    private String brief;
    private String mainCategoryName;
    private String subCategoryName;
    private Boolean publish;
    private List<StepRequestDto> steps;
    private Integer defaultImage;

    private Integer temporary;
    private String fileId;
}
