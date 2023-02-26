package com.company.semocheck.domain.request.category;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateSubCategoryRequestDto {
    private String mainName;
    private String subName;
}
