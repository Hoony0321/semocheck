package com.company.semocheck.domain.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DeleteBlocksRequest {
    List<Long> checklistIds;
}
