package com.company.semocheck.domain.request.member;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateBlocksRequest {
    private List<Long> ChecklistIds;
}
