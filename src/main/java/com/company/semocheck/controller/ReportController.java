package com.company.semocheck.controller;

import com.company.semocheck.common.response.*;
import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.Report;
import com.company.semocheck.domain.dto.SearchResultDto;
import com.company.semocheck.domain.dto.report.ReportDto;
import com.company.semocheck.domain.request.report.CreateReportRequest;
import com.company.semocheck.domain.request.report.UpdateReportRequest;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.ChecklistService;
import com.company.semocheck.service.MemberService;
import com.company.semocheck.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "신고", description = "신고 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    private final MemberService memberService;
    private final ChecklistService checklistService;


    //======= read method ======//
    @ApiDocumentResponse
    @Operation(summary = "get report by id API", description = "해당 번호의 신고를 조회합니다.\n\n")
    @GetMapping("/api/reports/{report_id}")
    private DataResponseDto<ReportDto> getReportById(@PathVariable("report_id") Long reportId){
        //TODO : 관리자 계정만 접근 가능하도록 수정해야함.

        Report report = reportService.findById(reportId);

        return DataResponseDto.of(ReportDto.createDto(report), Code.SUCCESS_READ);
    }

    @ApiDocumentResponse
    @Operation(summary = "get member's report API", description = "해당 회원의 신고를 조회합니다.\n\n")
    @GetMapping("/api/members/reports")
    private DataResponseDto<SearchResultDto<ReportDto>> getMemberReports(HttpServletRequest request){

        //get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        List<Report> reports = reportService.getMemberReports(member);
        List<ReportDto> reportDtos = new ArrayList<>();

        for(Report report : reports){
            reportDtos.add(ReportDto.createDto(report));
        }

        return DataResponseDto.of(SearchResultDto.createDto(reportDtos), Code.SUCCESS_READ);
    }

    //======= create method ======//
    @ApiDocumentResponse
    @Operation(summary = "create report API", description = "신고를 접수합니다.\n\n")
    @PostMapping("/api/members/reports")
    private DataResponseDto<Long> createReport(HttpServletRequest request, @RequestBody CreateReportRequest requestDto){

        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by checklistId
        if(requestDto.getChecklistId() == null) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_CHECKLIST );
        Checklist checklist = checklistService.findById(requestDto.getChecklistId());


        Long reportId = reportService.createReport(member, checklist, requestDto);

        return DataResponseDto.of(reportId, Code.SUCCESS_CREATE);
    }

    //======= update method ======//
    @ApiDocumentResponse
    @Operation(summary = "modify report API", description = "신고를 수정합니다.\n\n")
    @PutMapping("/api/members/reports/{report_id}")
    private DataResponseDto<Long> modifyReport(HttpServletRequest request, @PathVariable("report_id") Long reportId,
                                               @RequestBody UpdateReportRequest requestDto){

        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);
        //Get report by reportId
        Report report = reportService.findById(reportId);

        //validation
        List<Report> reports = member.getReports();
        if(!reports.contains(report)) throw new GeneralException(Code.FORBIDDEN);

        reportService.modifyReport(report, requestDto);

        return DataResponseDto.of(reportId, Code.SUCCESS_UPDATE);
    }

    //======= delete method ======//
    @ApiDocumentResponse
    @Operation(summary = "delete report API", description = "신고를 삭제합니다.\n\n")
    @DeleteMapping("/api/members/reports/{report_id}")
    private ResponseDto deleteReport(HttpServletRequest request, @PathVariable("report_id") Long reportId){

        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        reportService.deleteReport(reportId);

        return ResponseDto.of(true, Code.SUCCESS_DELETE);
    }

}
