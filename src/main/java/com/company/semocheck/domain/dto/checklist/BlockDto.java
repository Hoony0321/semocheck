package com.company.semocheck.domain.dto.checklist;

import com.company.semocheck.domain.checklist.Block;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BlockDto {

    private Long blockId;
    private Long checklistId;

    static public BlockDto createDto(Block entity){
        BlockDto dto = new BlockDto();
        dto.blockId = entity.getId();
        dto.checklistId = entity.getChecklist().getId();
        return dto;
    }
}
