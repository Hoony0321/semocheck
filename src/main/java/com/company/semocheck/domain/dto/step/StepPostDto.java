package com.company.semocheck.domain.dto.step;

import com.company.semocheck.domain.Step;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StepPostDto {
    private Long id;
    private String name;
    private String description;

    private Integer order;


    static public StepPostDto createDto(Step step) {
        StepPostDto dto = new StepPostDto();
        dto.id = step.getId();
        dto.name = step.getName();
        dto.description = step.getDescription();
        dto.order = step.getStepOrder();

        return dto;
    }
}
