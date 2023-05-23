package com.company.semocheck.controller.page.forms;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateCategoryForm {
    private String mainName;
    private String subName="";
}
