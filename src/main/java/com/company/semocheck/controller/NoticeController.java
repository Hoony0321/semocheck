package com.company.semocheck.controller;

import com.company.semocheck.common.response.*;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.Notice;
import com.company.semocheck.domain.dto.Role;
import com.company.semocheck.domain.dto.notice.CreateNoticeRequest;
import com.company.semocheck.domain.dto.notice.NoticeDto;
import com.company.semocheck.domain.dto.notice.UpdateNoticeRequest;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.MemberService;
import com.company.semocheck.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "공지사항", description = "공지사항 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;
    private final MemberService memberService;

    @ApiDocumentResponse
    @Operation(summary = "get all notices API", description = "모든 공지사항을 조회합니다.\n\n")
    @GetMapping("")
    public DataResponseDto<List<NoticeDto>> getAllNotices(){
        List<Notice> notices = noticeService.findAll();

        List<NoticeDto> noticeDtos = new ArrayList<>();
        for(Notice notice : notices){
            noticeDtos.add(NoticeDto.createDto(notice));
        }

        return DataResponseDto.of(noticeDtos, Code.SUCCESS_READ);
    }

    @ApiDocumentResponse
    @Operation(summary = "get notice by id API", description = "해당 id의 공지사항을 조회합니다.\n\n")
    @GetMapping("/{notice_id}")
    public DataResponseDto<NoticeDto> getNoticeById(@PathVariable("notice_id") Long noticeId){
        Notice notice = noticeService.findById(noticeId);
        return DataResponseDto.of(NoticeDto.createDto(notice), Code.SUCCESS_READ);
    }

    @ApiDocumentResponse
    @Operation(summary = "create notice API", description = "공지사항을 생성합니다.\n\n")
    @PostMapping("")
    public ResponseDto createNotice(HttpServletRequest request, @RequestBody CreateNoticeRequest requestDto){

        //TODO : admin 인증 함수로 단순화
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);
        if(!member.getRole().equals(Role.ADMIN)) throw new GeneralException(Code.FORBIDDEN);

        noticeService.createNotice(requestDto);
        return ResponseDto.of(true, Code.SUCCESS_CREATE);
    }

    @ApiDocumentResponse
    @Operation(summary = "update notice API", description = "공지사항을 수정합니다.\n\n")
    @PatchMapping("/{notice_id}")
    public ResponseDto updateNotice(HttpServletRequest request, @PathVariable("notice_id") Long noticeId, @RequestBody UpdateNoticeRequest requestDto){

        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);
        if(!member.getRole().equals(Role.ADMIN)) throw new GeneralException(Code.FORBIDDEN);

        noticeService.updateNotice(noticeId, requestDto);
        return ResponseDto.of(true, Code.SUCCESS_UPDATE);
    }

    @ApiDocumentResponse
    @Operation(summary = "delete notice API", description = "공지사항을 삭제합니다.\n\n")
    @DeleteMapping("/{notice_id}")
    public ResponseDto deleteNotice(HttpServletRequest request, @PathVariable("notice_id") Long noticeId){

        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);
        if(!member.getRole().equals(Role.ADMIN)) throw new GeneralException(Code.FORBIDDEN);

        noticeService.deleteNotice(noticeId);
        return ResponseDto.of(true, Code.SUCCESS_DELETE);
    }
}
