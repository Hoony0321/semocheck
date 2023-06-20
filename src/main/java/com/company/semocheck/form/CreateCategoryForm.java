package com.company.semocheck.form;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateCategoryForm {
    private String mainName;
    private String subName="";
}
