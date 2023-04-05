package com.company.semocheck.domain.request.checklist;

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
    private Integer defaultImage;
    private List<StepRequestDto> steps;
}
