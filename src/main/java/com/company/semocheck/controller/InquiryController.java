package com.company.semocheck.controller;

import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.inquiry.Inquiry;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.dto.Role;
import com.company.semocheck.domain.dto.SearchResultDto;
import com.company.semocheck.domain.dto.inquiry.InquiryDetailDto;
import com.company.semocheck.domain.dto.inquiry.InquirySimpleDto;
import com.company.semocheck.domain.request.inquiry.CreateInquiryCommentRequest;
import com.company.semocheck.domain.request.inquiry.CreateInquiryRequest;
import com.company.semocheck.domain.request.inquiry.UpdateInquiryRequest;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.InquiryCommentService;
import com.company.semocheck.service.InquiryService;
import com.company.semocheck.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "문의", description = "1대1 문의 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    private final InquiryCommentService inquiryCommentService;
    private final MemberService memberService;

    //======= read method ======//
    @ApiDocumentResponse
    @Operation(summary = "get member's inquiry list API", description = "해당 회원의 문의 리스트를 조회합니다.\n\n")
    @GetMapping("/api/members/inquiries")
    private DataResponseDto<SearchResultDto> getInquiries(HttpServletRequest request){
        //get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        List<Inquiry> inquiries = inquiryService.findByMember(member);
        List<InquirySimpleDto> inquirySimpleDtoList = new ArrayList<>();

        for(Inquiry inquiry : inquiries){
            inquirySimpleDtoList.add(InquirySimpleDto.createDto(inquiry));
        }

        return DataResponseDto.of(SearchResultDto.createDto(inquirySimpleDtoList), Code.SUCCESS_READ);
    }

    @ApiDocumentResponse
    @Operation(summary = "get inquiry by id API", description = "해당 번호의 문의를 조회합니다.\n\n" +
            "[Inquiry Status] : STAND_BY / COMPLETED")
    @GetMapping("/api/members/inquiries/{inquiry_id}")
    private DataResponseDto<InquiryDetailDto> getInquiryById(HttpServletRequest request, @PathVariable("inquiry_id") Long inquiryId){
        //get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        Inquiry inquiry = inquiryService.findById(inquiryId);
        if(!inquiry.getMember().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        return DataResponseDto.of(InquiryDetailDto.createDto(inquiry), Code.SUCCESS_READ);
    }

    //======= create method ======//
    @ApiDocumentResponse
    @Operation(summary = "create inquiry API", description = "문의를 생성합니다.\n\n")
    @PostMapping("/api/members/inquiries")
    private ResponseDto createInquiry(HttpServletRequest request, @RequestBody CreateInquiryRequest createRequest){
        //get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //validate request
        createRequest.validate();

        //create inquiry
        inquiryService.createInquiry(member, createRequest);

        return ResponseDto.of(true, Code.SUCCESS_CREATE);
    }

    @ApiDocumentResponse
    @Operation(summary = "create inquiry comment API", description = "문의에 댓글을 생성합니다.\n\n")
    @PostMapping("/api/members/inquiries/{inquiry_id}/comments")
    private ResponseDto createInquiryComment(HttpServletRequest request, @PathVariable("inquiry_id") Long inquiryId, @RequestBody CreateInquiryCommentRequest createRequest){
        //get member by jwt token
        Member writer = memberService.getMemberByJwt(request);
        if(writer.getRole().equals(Role.ADMIN.toString())) throw new GeneralException(Code.FORBIDDEN);

        //validate request
        createRequest.validate();

        //find inquiry by id
        Inquiry inquiry = inquiryService.findById(inquiryId);

        //create inquiry comment
        inquiryCommentService.createInquiryComment(inquiry, writer, createRequest);

        return ResponseDto.of(true, Code.SUCCESS_CREATE);
    }


    //======= update method ======//
    @ApiDocumentResponse
    @Operation(summary = "update inquiry API", description = "문의를 수정합니다.\n\n")
    @PutMapping("/api/members/inquiries/{inquiry_id}")
    private ResponseDto updateInquiry(HttpServletRequest request, @PathVariable("inquiry_id") Long inquiryId, @RequestBody UpdateInquiryRequest updateRequest){
        //get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //validate request
        updateRequest.validate();

        //find inquiry by id
        Inquiry inquiry = inquiryService.findById(inquiryId);
        if(!inquiry.getMember().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        //update inquiry
        inquiryService.updateInquiry(inquiry, updateRequest);

        return ResponseDto.of(true, Code.SUCCESS_UPDATE);
    }

    //======= delete method ======//
    @ApiDocumentResponse
    @Operation(summary = "delete inquiry API", description = "문의를 삭제합니다.\n\n")
    @DeleteMapping("/api/members/inquiries/{inquiry_id}")
    private ResponseDto deleteInquiry(HttpServletRequest request, @PathVariable("inquiry_id") Long inquiryId){
        //get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //find inquiry by id
        Inquiry inquiry = inquiryService.findById(inquiryId);
        if(!inquiry.getMember().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        //delete inquiry
        inquiryService.deleteInquiry(inquiry);

        return ResponseDto.of(true, Code.SUCCESS_DELETE);
    }
}
