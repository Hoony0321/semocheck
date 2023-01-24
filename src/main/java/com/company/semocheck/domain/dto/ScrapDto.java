package com.company.semocheck.domain.dto;

import com.company.semocheck.domain.CheckList;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.Scrap;
import com.company.semocheck.domain.dto.checklist.CheckListPostDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScrapDto {

    private Long scrapId;
    private Long checkListId;

    static public ScrapDto createDto(Scrap entity){
        ScrapDto dto = new ScrapDto();
        dto.scrapId = entity.getId();
        dto.checkListId = entity.getCheckList().getId();
        return dto;
    }
}
