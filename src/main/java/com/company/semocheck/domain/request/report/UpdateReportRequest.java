package com.company.semocheck.domain.request.report;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateReportRequest {
    private String type;
    private String content;
}
