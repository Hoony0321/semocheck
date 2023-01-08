package com.company.semocheck.domain.dto.request.checkList;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateStepRequestDto {
    public List<StepRequestDto> steps;
}
