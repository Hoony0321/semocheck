package com.company.semocheck.domain.request.checklist;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StepRequestDto {

    public Long stepId;
    public String name;
    public Integer order;
    public String description;

    @Builder
    public StepRequestDto(Long stepId, String name, Integer order, String description) {
        this.stepId = stepId;
        this.name = name;
        this.order = order;
        this.description = description;
    }
}
