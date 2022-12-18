package com.company.semocheck.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TestDto {

    @Schema(description = "이름", example = "name")
    private String name;

    @Schema(description = "번호", example = "1")
    private Integer number;

    @Schema(description = "데이터", example = "data")
    private Integer data;

    @Builder
    public TestDto(String name, Integer number, Integer data) {
        this.name = name;
        this.number = number;
        this.data = data;
    }
}
