package com.company.semocheck.domain.dto.request.member;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JoinRequestDto {
    private Boolean sex;
    private Integer age;
    private Boolean agreeNotify;
}
