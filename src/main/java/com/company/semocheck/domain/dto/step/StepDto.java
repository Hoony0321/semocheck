package com.company.semocheck.domain.dto.step;

import com.company.semocheck.domain.Step;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StepDto {

    private Long id;
    private String name;
    private Integer order;
    private String description;

    private Boolean isCheck;

    static public StepDto createDto(Step step){
        StepDto dto = new StepDto();
        dto.id = step.getId();
        dto.name = step.getName();
        dto.order = step.getStepOrder();
        dto.description = step.getDescription();
        dto.isCheck = step.getIsCheck();
        return dto;
    }
}
