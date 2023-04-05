package com.company.semocheck.domain.request.checklist;

import lombok.Data;

import java.util.List;

@Data

public class DeleteTempChecklistRequest {
    private List<Long> checklistIds;
}
