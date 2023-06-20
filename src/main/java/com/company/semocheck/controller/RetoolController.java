package com.company.semocheck.controller;

import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.dto.FileDto;
import com.company.semocheck.domain.dto.Role;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.form.CreateChecklistForm;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.service.FileService;
import com.company.semocheck.service.MemberService;
import com.company.semocheck.service.checklist.ChecklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "리툴", description = "리툴 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/retool")
public class RetoolController {

    private final MemberService memberService;
    private final ChecklistService checklistService;

    private final CategoryController categoryController;

    //TODO : admin path url 포함시키기
    @ApiDocumentResponse
    @Operation(summary = "Create new checklist API by admin", description = "[어드민 전용] 새로운 체크리스트를 생성합니다.\n\n" +
            "필수 목록 : [title]")
    @PostMapping(value = "/checklists")
    private ResponseDto createChecklist(@RequestBody CreateChecklistForm createForm){
        Member admin = memberService.findById(1l);

        // validate requestDto
        createForm.validate();

        // image setting
        if(createForm.getImageId() == null){
            DataResponseDto<FileDto> defaultImageFileDto = categoryController.getDefaultImage(createForm.getMainCategoryName(), createForm.getSubCategoryName());
            createForm.setDefaultImageId(defaultImageFileDto.getData().getId());
        }

        // create a checklist Entity
        Long checklistId = checklistService.createChecklist(createForm, admin);

        return DataResponseDto.of(checklistId, Code.SUCCESS_CREATE);
    }

    @ApiDocumentResponse
    @Operation(summary = "Delete a checklist API", description = "[어드민 전용] 회원의 체크리스트를 삭제합니다.\n\n")
    @DeleteMapping("/checklists/{checklist_id}")
    private ResponseDto deleteChecklist(@PathVariable("checklist_id") Long checklistId){
        Member admin = memberService.findById(1l);

        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);

        //Delete checklist
        checklistService.deleteChecklist(checklist, admin);

        return ResponseDto.of(true, Code.SUCCESS_DELETE);
    }

}
