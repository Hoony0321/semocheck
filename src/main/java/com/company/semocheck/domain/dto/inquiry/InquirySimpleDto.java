package com.company.semocheck.domain.dto.inquiry;

import com.company.semocheck.domain.Inquiry;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class InquirySimpleDto {
    private Long id;
    private String title;
    private String status;
    private String createdDate;

    static public InquirySimpleDto createDto(Inquiry inquiry){
        InquirySimpleDto dto = new InquirySimpleDto();
        dto.id = inquiry.getId();
        dto.title = inquiry.getTitle();
        dto.status = inquiry.getStatus().toString();
        dto.createdDate = inquiry.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        return dto;
    }
}
