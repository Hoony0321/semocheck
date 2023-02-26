package com.company.semocheck.domain.request.member;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateRequestDto {

    private String name;

    private String picture;

    private Boolean agreeNotify;

    private Boolean sex;

    private Integer age;

}
