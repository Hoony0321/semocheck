package com.company.semocheck.domain.dto;

import com.company.semocheck.domain.StepItem;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StepDto {

    private Long id;
    private String name;
    private Integer order;
    private String description;

    static public StepDto createDto(StepItem stepItem){
        StepDto dto = new StepDto();
        dto.id = stepItem.getId();
        dto.name = stepItem.getName();
        dto.order = stepItem.getStepOrder();
        dto.description = stepItem.getDescription();
        return dto;
    }
}
