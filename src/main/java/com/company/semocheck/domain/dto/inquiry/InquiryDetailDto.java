package com.company.semocheck.domain.dto.inquiry;

import com.company.semocheck.domain.Inquiry;
import com.company.semocheck.domain.InquiryComment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class InquiryDetailDto {

    private Long id;
    private String title;
    private String content;
    private String status;
    private String createdDate;
    private List<InquiryCommentDto> comments = new ArrayList<>();

    static public InquiryDetailDto createDto(Inquiry inquiry){
        InquiryDetailDto dto = new InquiryDetailDto();
        dto.id = inquiry.getId();
        dto.title = inquiry.getTitle();
        dto.content = inquiry.getContent();
        dto.status = inquiry.getStatus().toString();
        dto.createdDate = inquiry.getCreatedDate().toString();

        for(InquiryComment comment : inquiry.getInquiryComments()){
            dto.comments.add(InquiryCommentDto.createDto(comment));
        }
        return dto;
    }
}
