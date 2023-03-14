package com.company.semocheck.domain.request.memberCategory;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteMemberCategoryRequest {
    private String mainName;
    private String subName;
}
