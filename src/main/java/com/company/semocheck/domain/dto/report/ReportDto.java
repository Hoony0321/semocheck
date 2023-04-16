package com.company.semocheck.domain.dto.report;

import com.company.semocheck.domain.Report;
import com.company.semocheck.domain.dto.ReportStatus;
import com.company.semocheck.domain.dto.ReportType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportDto {

    private Long id;
    private Long checklistId;
    private ReportType type;
    private ReportStatus status;
    private String content;




    static public ReportDto createDto(Report report) {
        ReportDto dto = new ReportDto();
        dto.id = report.getId();
        dto.checklistId = report.getChecklist().getId();
        dto.type = report.getType();
        dto.status = report.getStatus();
        dto.content = report.getContent();

        return dto;
    }
}
