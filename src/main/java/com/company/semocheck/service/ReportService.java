package com.company.semocheck.service;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.Report;
import com.company.semocheck.domain.request.report.CreateReportRequest;
import com.company.semocheck.domain.request.report.UpdateReportRequest;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;

    public Report findById(Long id){
        Optional<Report> findOne = reportRepository.findById(id);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND);

        return findOne.get();
    }

    public List<Report> getMemberReports(Member member){
        return member.getReports();
    }

    @Transactional
    public Long createReport(Member member, Checklist checklist, CreateReportRequest requestDto){
        Report report = Report.createEntity(member, checklist, requestDto);

        //연관관계 설정
        report.setWriter(member);
        member.addReport(report);

        reportRepository.save(report);

        return report.getId();
    }

    @Transactional
    public Report modifyReport(Report report, UpdateReportRequest requestDto){
        report.modify(requestDto);
        return report;
    }

    @Transactional
    public void deleteReport(Long id){
        Report report = findById(id);

        //연관관계 설정
        Member writer = report.getWriter();
        writer.removeReport(report);

        reportRepository.delete(report);
    }
}
