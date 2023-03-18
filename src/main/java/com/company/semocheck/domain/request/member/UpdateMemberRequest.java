package com.company.semocheck.domain.request.member;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateMemberRequest {

    private String name;

    private String picture;

    private Boolean agreeNotify;

    private Boolean sex;

    private Integer age;

    @Builder
    public UpdateMemberRequest(String name, String picture, Boolean agreeNotify, Boolean sex, Integer age) {
        this.name = name;
        this.picture = picture;
        this.agreeNotify = agreeNotify;
        this.sex = sex;
        this.age = age;
    }
}
