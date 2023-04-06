package com.company.semocheck.domain.request.inquiry;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateInquiryRequest {
    private String title;
    private String content;
}
