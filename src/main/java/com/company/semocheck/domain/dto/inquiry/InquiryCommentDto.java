package com.company.semocheck.domain.dto.inquiry;

import com.company.semocheck.domain.Inquiry;
import com.company.semocheck.domain.InquiryComment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class InquiryCommentDto {
    private Long id;
    private String writer;
    private String content;
    private String createdDate;

    static public InquiryCommentDto createDto(InquiryComment inquiryComment){
        InquiryCommentDto dto = new InquiryCommentDto();
        dto.id = inquiryComment.getId();
        dto.writer = inquiryComment.getWriter().getName();
        dto.content = inquiryComment.getContent();
        dto.createdDate = inquiryComment.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        return dto;
    }
}
