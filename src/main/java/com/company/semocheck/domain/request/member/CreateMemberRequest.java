package com.company.semocheck.domain.request.member;

import com.company.semocheck.domain.dto.category.SubCategoryDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateMemberRequest {
    private Boolean agreeNotify;
    private String name;
    private Boolean sex;
    private Integer age;
    private List<SubCategoryDto> categories;

    @Builder
    public CreateMemberRequest(Boolean agreeNotify, String name, Boolean sex, Integer age, List<SubCategoryDto> categories) {
        this.agreeNotify = agreeNotify;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.categories = categories;
    }
}
