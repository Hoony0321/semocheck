package com.company.semocheck.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class SearchResultDto<T> {
    private long count;
    private List<T> entities = new ArrayList<>();

    static public <T> SearchResultDto createDto(List<T> entities){
        SearchResultDto<T> dto = new SearchResultDto<T>();
        dto.count = entities.size();
        dto.entities = entities;
        return dto;
    }
}
