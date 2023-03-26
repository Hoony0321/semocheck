package com.company.semocheck.domain.request.memberCategory;

import com.company.semocheck.domain.dto.category.MemberCategoryDto;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UpdateMemberCategoryRequest {
    List<MemberCategoryDto> categories;
}
