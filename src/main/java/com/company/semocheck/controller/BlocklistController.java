package com.company.semocheck.controller;

import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.checklist.BlockedChecklist;
import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.dto.SearchResultDto;
import com.company.semocheck.domain.dto.checklist.BlockedChecklistDto;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.service.MemberService;
import com.company.semocheck.service.checklist.BlockedChecklistService;
import com.company.semocheck.service.checklist.ChecklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "차단리스트", description = "차단리스트 관련 API 모음입니다.")
@RequiredArgsConstructor
public class BlocklistController {

    private final BlockedChecklistService blockedChecklistService;
    private final MemberService memberService;
    private final ChecklistService checklistService;


    @ApiDocumentResponse
    @Operation(summary = "차단리스트 조회 API", description = "회원의 차단리스트를 조회합니다.\n\n")
    @GetMapping("/api/members/blocklists")
    private DataResponseDto<SearchResultDto> getBlocklists(HttpServletRequest request){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get blocklists by member
        List<BlockedChecklist> blocklists = blockedChecklistService.getBlocklists(member);

        //Return blocklists to searchResults
        List<BlockedChecklistDto> blockedChecklistDtos = new ArrayList<>();
        for(BlockedChecklist blockedChecklist : blocklists){
            blockedChecklistDtos.add(BlockedChecklistDto.createDto(blockedChecklist));
        }

        return DataResponseDto.of(SearchResultDto.createDto(blockedChecklistDtos), Code.SUCCESS_READ);
    }

    @ApiDocumentResponse
    @Operation(summary = "차단리스트 추가 API", description = "회원의 차단리스트에 추가합니다.\n\n")
    @PostMapping("/api/members/blocklists/{checklist_id}")
    private ResponseDto addBlocklist(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);

        //Insert stepItem into checklist entity
        blockedChecklistService.createBlockedChecklist(member, checklist);

        return ResponseDto.of(true, Code.SUCCESS_CREATE);
    }

    @ApiDocumentResponse
    @Operation(summary = "차단리스트 삭제 API", description = "회원의 차단리스트에서 삭제합니다.\n\n")
    @DeleteMapping("/api/members/blocklists/{checklist_id}")
    private ResponseDto deleteBlocklist(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);

        //Insert stepItem into checklist entity
        blockedChecklistService.deleteBlockedChecklist(member, checklist);

        return ResponseDto.of(true, Code.SUCCESS_DELETE);
    }
}
