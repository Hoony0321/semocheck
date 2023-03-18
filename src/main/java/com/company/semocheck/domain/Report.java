package com.company.semocheck.domain;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.domain.dto.ReportStatus;
import com.company.semocheck.domain.dto.ReportType;
import com.company.semocheck.domain.request.report.CreateReportRequest;
import com.company.semocheck.domain.request.report.UpdateReportRequest;
import com.company.semocheck.exception.GeneralException;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @NotNull
    @JsonIgnore
    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReportType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @NotNull
    @Size(max = 500)
    private String content;

    static public Report createEntity(Member member, CreateReportRequest requestDto){
        Report entity = new Report();
        entity.content = requestDto.getContent();
        entity.status = ReportStatus.IN_PROGRESS;

        ReportType reportType = entity.getType(requestDto.getType());
        entity.type = reportType;

        return entity;
    }

    //연관관계 메서드
    public void setWriter(Member member){
        this.writer = member;
    }

    public void modify(UpdateReportRequest requestDto) {
        ReportType reportType = this.getType(requestDto.getType());
        this.type = reportType;
        this.content = requestDto.getContent();
    }

    private ReportType getType(String type){
        try{
            return ReportType.valueOf(type);
        }catch (IllegalArgumentException e){
            throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.INVALID_REPORT_TYPE);
        }
    }
}
