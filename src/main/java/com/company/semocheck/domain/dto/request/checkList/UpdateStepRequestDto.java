package com.company.semocheck.domain.dto.request.checkList;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UpdateStepRequestDto {
    List<StepUpdateDto> steps;
}
