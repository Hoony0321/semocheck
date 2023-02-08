package com.company.semocheck.controller;

import com.company.semocheck.auth.jwt.JwtUtils;
import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.dto.ScrapDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.ChecklistService;
import com.company.semocheck.service.MemberService;
import com.company.semocheck.service.ScrapService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "스크랩", description = "스크랩 관련 API 모음입니다.")
@RequiredArgsConstructor
public class ScrapController {

    private final ScrapService scrapService;
    private final MemberService memberService;
    private final ChecklistService checklistService;

    @ApiDocumentResponse
    @Operation(summary = "Scrap checklist API", description = "해당 체크리스트를 스크랩합니다.\n\n" +
            "회원의 체크리스트인 경우 스크랩할 수 없습니다. - 400 Bad request error\n\n")
    @PostMapping("/api/members/scraps/{checklist_id}")
    private ResponseDto createScrap(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);
        if(checklist.getOwner().equals(member)) throw new GeneralException(Code.BAD_REQUEST, "자신의 체크리스트를 스크랩할 수 없습니다.");

        //Insert stepItem into checklist entity
        scrapService.createScrap(member, checklist);

        return ResponseDto.of(true, "스크랩 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Get member's scrap list API", description = "회원의 스크랩 리스트를 조회합니다.\n\n")
    @GetMapping("/api/members/scraps")
    private DataResponseDto<List<ScrapDto>> getScraps(HttpServletRequest request){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Insert stepItem into checklist entity
        List<ScrapDto> dtos = scrapService.getScrap(member);

        return DataResponseDto.of(dtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Delete member's scrap API", description = "회원의 스크랩을 checklist Id를 통해 삭제합니다.\n\n")
    @DeleteMapping("/api/members/scraps/{checklist_id}")
    private ResponseDto deleteScrap(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);

        //Insert stepItem into checklist entity
        scrapService.deleteScrap(member, checklist);

        return ResponseDto.of(true, "삭제 성공");
    }
}
