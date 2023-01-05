package com.company.semocheck.domain.dto.request.category;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateSubCategoryRequestDto {
    private String mainName;
    private String subName;
}
