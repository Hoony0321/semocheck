package com.company.semocheck.domain.request.checklist;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StepUpdateDto {
    private Long stepId;
    private Boolean isCheck;

}
