package com.company.semocheck.domain.dto.request.checkList;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StepUpdateDto {
    private Long stepId;
    private Boolean isCheck;

}
