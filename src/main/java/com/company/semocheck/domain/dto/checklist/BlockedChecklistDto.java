package com.company.semocheck.domain.dto.checklist;

import com.company.semocheck.domain.checklist.BlockedChecklist;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BlockedChecklistDto {

    private Long blockId;
    private Long checklistId;

    static public BlockedChecklistDto createDto(BlockedChecklist entity){
        BlockedChecklistDto dto = new BlockedChecklistDto();
        dto.blockId = entity.getId();
        dto.checklistId = entity.getChecklist().getId();
        return dto;
    }
}
