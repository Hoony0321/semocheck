package com.company.semocheck.controller;

import com.company.semocheck.common.response.*;
import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.dto.SearchResultDto;
import com.company.semocheck.domain.dto.checklist.ChecklistTempDetailDto;
import com.company.semocheck.domain.dto.checklist.ChecklistTempSimpleDto;
import com.company.semocheck.domain.request.checklist.CreateChecklistRequest;
import com.company.semocheck.domain.request.checklist.DeleteTempChecklistRequest;
import com.company.semocheck.domain.request.checklist.UpdateChecklistRequestDto;
import com.company.semocheck.domain.request.tempChecklist.CreateTempChecklistRequest;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.MemberService;
import com.company.semocheck.service.TempChecklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "임시 체크리스트", description = "임시 체크리스트 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
public class TempChecklistController {
    private final MemberService memberService;
    private final TempChecklistService tempChecklistService;

    @ApiDocumentResponse
    @Operation(summary = "Get member's temporay checklists API", description = "회원의 임시 체크리스트 목록을 조회합니다.\n\n" +
            "해당 멤버 소유의 체크리스트만 접근 가능합니다.")
    @GetMapping("/api/members/checklists/temp")
    public DataResponseDto<SearchResultDto<ChecklistTempSimpleDto>> getMemberTempChecklists(HttpServletRequest request){
        //get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //get member's temp checklists
        List<Checklist> checklists = member.getTempChecklists();

        //convert to dto
        List<ChecklistTempSimpleDto> checklistTempSimpleDtos = new ArrayList<>();
        checklists.stream().forEach(chk -> checklistTempSimpleDtos.add(ChecklistTempSimpleDto.createDto(chk)));

        return DataResponseDto.of(SearchResultDto.createDto(checklistTempSimpleDtos), Code.SUCCESS_READ);
    }

    @ApiDocumentResponse
    @Operation(summary = "Get member's temporay checklist by id API", description = "회원의 임시 체크리스트를 id를 통해 조회합니다.\n\n" +
            "해당 멤버 소유의 체크리스트만 접근 가능합니다.")
    @GetMapping("/api/members/checklists/temp/{checklist_id}")
    public DataResponseDto<ChecklistTempDetailDto> getMemberTempChecklistById(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        Checklist checklist = tempChecklistService.findById(checklistId);
        if(!checklist.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        return DataResponseDto.of(ChecklistTempDetailDto.createDto(checklist), Code.SUCCESS_READ);
    }

    //======= create method ======//
    @ApiDocumentResponse
    @Operation(summary = "Create new temporary checklist API", description = "새로운 임시 체크리스트를 생성합니다.\n\n" +
            "[required] temporary is not null")
    @PostMapping(value = "/api/members/checklists/temp")
    private ResponseDto createChecklist(HttpServletRequest request, @RequestBody CreateTempChecklistRequest requestDto){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //validate requestDto
        if(requestDto.getTemporary() == null) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.REQUIRED_FIELD_TEMPORARY);

        //Create a checklist Entity
        Long checklistId = tempChecklistService.createChecklist(requestDto, member);

        return DataResponseDto.of(checklistId, Code.SUCCESS_CREATE);
    }

    @ApiDocumentResponse
    @Operation(summary = "Update temp checklist's info API", description = "임시 체크리스트 정보를 수정합니다.\n\n" +
            "회원의 체크리스트가 아닌 경우 수정이 불가능합니다. - 403 Forbidden error\n\n" +
            "temporary field에 null이 들어가면 publish한 것으로 간주됩니다.\n\n" +
            "===step list 관련 내용=== \n\n" +
            "step list 부분은 수정하든 안 하든 일단 포함해서 넘겨줘야 합니다.\n\n" +
            "수정을 하지 않더라도 기존 step id에 기존 정보를 포함시켜서 넘겨줘야 합니다.\n\n" +
            "step list 부분은 기존 정보를 넘기지 않을 시 삭제된 것으로 간주됩니다.\n\n" +
            "step id가 -1인 경우는 새로 추가된 step으로 간주됩니다.")
    @PutMapping("/api/members/checklists/temp/{checklist_id}")
    private ResponseDto updateChecklistInfo(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId, @RequestBody UpdateChecklistRequestDto requestDto){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = tempChecklistService.findById(checklistId);
        if(!checklist.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        //Update checklist's info Entity
        tempChecklistService.updateChecklist(checklist, requestDto);

        return ResponseDto.of(true, Code.SUCCESS_UPDATE);
    }

    //======= delete method ======//
    @ApiDocumentResponse
    @Operation(summary = "Delete a temp checklist by id API", description = "회원의 임시 체크리스트를 삭제합니다.\n\n" +
            "회원의 체크리스트가 아닌 경우 삭제가 불가능합니다. - 403 Forbidden error")
    @DeleteMapping("/api/members/checklists/temp/{checklist_id}")
    private ResponseDto deleteChecklistById(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = tempChecklistService.findById(checklistId);
        if(!checklist.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        //Delete checklist
        tempChecklistService.deleteChecklist(checklist, member);

        return ResponseDto.of(true, Code.SUCCESS_DELETE);
    }


    @ApiDocumentResponse
    @Operation(summary = "Delete a temp checklist API", description = "회원의 임시 체크리스트를 삭제합니다.\n\n" +
            "회원의 체크리스트가 아닌 경우 삭제가 불가능합니다. - 403 Forbidden error")
    @DeleteMapping("/api/members/checklists")
    private ResponseDto deleteChecklistByList(HttpServletRequest request, @RequestBody DeleteTempChecklistRequest requestDto){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        requestDto.getChecklistIds().stream().forEach(id -> {
            Checklist checklist = tempChecklistService.findById(id);
            if(!checklist.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

            //Delete checklist
            tempChecklistService.deleteChecklist(checklist, member);
        });



        return ResponseDto.of(true, Code.SUCCESS_DELETE);
    }

}
