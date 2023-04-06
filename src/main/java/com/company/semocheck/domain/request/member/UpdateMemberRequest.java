package com.company.semocheck.domain.request.member;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateMemberRequest {

    private String name;
    private Boolean agreeNotify;

    private Integer age;

    @Builder
    public UpdateMemberRequest(String name, Boolean agreeNotify, Integer age) {
        this.name = name;
        this.agreeNotify = agreeNotify;
        this.age = age;
    }
}
