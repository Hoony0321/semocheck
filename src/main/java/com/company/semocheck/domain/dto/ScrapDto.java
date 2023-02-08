package com.company.semocheck.domain.dto;

import com.company.semocheck.domain.Scrap;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScrapDto {

    private Long scrapId;
    private Long checklistId;

    static public ScrapDto createDto(Scrap entity){
        ScrapDto dto = new ScrapDto();
        dto.scrapId = entity.getId();
        dto.checklistId = entity.getChecklist().getId();
        return dto;
    }
}
