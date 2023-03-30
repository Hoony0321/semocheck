package com.company.semocheck.domain;


import com.company.semocheck.domain.dto.NoticeType;
import com.company.semocheck.domain.dto.ReportType;
import com.company.semocheck.domain.dto.notice.CreateNoticeRequest;
import com.company.semocheck.domain.dto.notice.UpdateNoticeRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
public class Notice extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @NotNull
    @Size(max = 50)
    private String title;

    @NotNull
    @Size(max = 1000)
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NoticeType type;

    public static Notice createEntity(CreateNoticeRequest request){
        Notice notice = new Notice();
        notice.title = request.getTitle();
        notice.content = request.getContent();
        notice.type = NoticeType.valueOf(request.getType());
        return notice;
    }

    public void update(UpdateNoticeRequest request) {
        if(request.getTitle() != null) this.title = request.getTitle();
        if(request.getContent() != null) this.content = request.getContent();
        if(request.getType() != null) this.type = NoticeType.valueOf(request.getType());
    }
}
