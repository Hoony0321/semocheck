package com.company.semocheck.domain.dto.notice;

import com.company.semocheck.domain.Notice;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NoticeDto {
    private Long id;
    private String title;
    private String content;
    private String noticeType;

    static public NoticeDto createDto(Notice notice) {
        NoticeDto dto = new NoticeDto();
        dto.id = notice.getId();
        dto.title = notice.getTitle();
        dto.content = notice.getContent();
        dto.noticeType = notice.getType().toString();
        return dto;
    }
}
