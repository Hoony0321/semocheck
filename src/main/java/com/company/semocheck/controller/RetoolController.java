package com.company.semocheck.controller;

import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.request.checklist.CreateChecklistRequest;
import com.company.semocheck.service.MemberService;
import com.company.semocheck.service.checklist.ChecklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "리툴", description = "리툴 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/retool")
public class RetoolController {

    private final MemberService memberService;
    private final ChecklistService checklistService;

    //TODO : admin path url 포함시키기
    @ApiDocumentResponse
    @Operation(summary = "Create new checklist API by admin", description = "[어드민 전용] 새로운 체크리스트를 생성합니다.\n\n" +
            "필수 목록 : [title]")
    @PostMapping(value = "/checklists")
    private ResponseDto createChecklist(@RequestBody CreateChecklistRequest requestDto){
        // get member by jwt token
        Member admin = memberService.findById(1l);

        // validate requestDto
        requestDto.validate();

        // create a checklist Entity
        Long checklistId = checklistService.createChecklist(requestDto, admin);

        return DataResponseDto.of(checklistId, Code.SUCCESS_CREATE);
    }

}
