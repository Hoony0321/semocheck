package com.company.semocheck.domain.request.checklist;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StepRequestDto {

    private Long stepId;
    private String name;
    private Integer order;
    private String description;

    @Builder
    public StepRequestDto(Long stepId, String name, Integer order, String description) {
        this.stepId = stepId;
        this.name = name;
        this.order = order;
        this.description = description;
    }
}
