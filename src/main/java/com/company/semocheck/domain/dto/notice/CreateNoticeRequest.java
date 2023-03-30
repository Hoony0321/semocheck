package com.company.semocheck.domain.dto.notice;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateNoticeRequest {
    private String title;
    private String content;
    private String type;
}
