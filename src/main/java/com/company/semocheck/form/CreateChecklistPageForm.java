package com.company.semocheck.form;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CreateChecklistPageForm {

    private String title;
    private String brief;
    private String subCategoryId;
    private Boolean publish;
    private List<CreateStepForm> steps = new ArrayList<>();

}
