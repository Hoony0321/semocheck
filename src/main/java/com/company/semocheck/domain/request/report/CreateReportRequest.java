package com.company.semocheck.domain.request.report;

import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.dto.ReportType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateReportRequest {
    private String type;
    private String content;
}
