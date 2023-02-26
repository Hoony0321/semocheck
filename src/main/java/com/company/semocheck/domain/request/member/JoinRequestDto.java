package com.company.semocheck.domain.request.member;

import com.company.semocheck.domain.dto.category.SubCategoryDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class JoinRequestDto {
    private Boolean agreeNotify;
    private Boolean sex;
    private Integer age;
    private List<SubCategoryDto> categories;
}
