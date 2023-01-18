package com.company.semocheck.domain.dto.request.checkList;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StepRequestDto {

    private Long stepId;
    private String name;
    private Integer order;
    private String description;

}
