package com.company.semocheck.domain.request.tempChecklist;

import lombok.Data;

import java.util.List;

@Data

public class DeleteTempChecklistRequest {
    private List<Long> checklistIds;
}
