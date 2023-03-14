package com.company.semocheck.controller.page.forms;

import com.company.semocheck.domain.SubCategory;
import com.company.semocheck.domain.dto.step.StepDto;
import com.company.semocheck.domain.request.checklist.CreateStepRequestDto;
import com.company.semocheck.domain.request.checklist.StepRequestDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CreateChecklistForm {
    private String title;
    private String brief;
    private String subCategoryId;
    private Boolean publish;
    private List<StepRequestDto> steps = new ArrayList<>();
}
