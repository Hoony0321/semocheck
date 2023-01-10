package com.company.semocheck.domain.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TestRequestDto {

    private List<String> datas;
}
