package com.company.semocheck.domain.request.report;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateReportRequest {
    private Long checklistId;
    private String type;
    private String content;
}
