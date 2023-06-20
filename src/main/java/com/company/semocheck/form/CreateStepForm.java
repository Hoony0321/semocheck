package com.company.semocheck.form;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateStepForm {
    String stepName;
    String stepDescription;

    @Builder
    public CreateStepForm(String stepName, String stepDescription) {
        this.stepName = stepName;
        this.stepDescription = stepDescription;
    }
}
