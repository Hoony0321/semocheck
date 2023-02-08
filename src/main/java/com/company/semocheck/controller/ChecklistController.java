package com.company.semocheck.controller;

import com.company.semocheck.auth.jwt.JwtUtils;
import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.dto.checklist.ChecklistDetailDto;
import com.company.semocheck.domain.dto.checklist.ChecklistPostDto;
import com.company.semocheck.domain.dto.request.checklist.CreateChecklistRequestDto;
import com.company.semocheck.domain.dto.request.checklist.UpdateChecklistRequestDto;
import com.company.semocheck.domain.dto.request.checklist.UpdateStepRequestDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.CategoryService;
import com.company.semocheck.service.ChecklistService;
import com.company.semocheck.service.MemberService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "체크리스트", description = "체크리스트 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
public class ChecklistController {

    private final MemberService memberService;
    private final ChecklistService checklistService;

    private final CategoryService categoryService;
    private final JwtUtils jwtUtils;


    //======= read method ======//
    @ApiDocumentResponse
    @Operation(summary = "Get all visibile checklist API", description = "공개된 모든 체크리스트를 조회합니다.\n\n")
    @GetMapping("/api/checklists")
    private DataResponseDto<List<ChecklistPostDto>> getAllVisibleChecklists(HttpServletRequest request){
        List<Checklist> checklists = checklistService.getAllVisibleChecklists();

        //entity convert to dto
        List<ChecklistPostDto> checklistPostDtos = new ArrayList<>();
        for (Checklist checklist : checklists) {
            checklistPostDtos.add(ChecklistPostDto.createDto(checklist));
        }

        return DataResponseDto.of(checklistPostDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "[Test] Query checklist API", description = "쿼리문을 통해 체크리스트를 조회합니다.(테스트중)\n\n")
    @GetMapping("/api/checklists/query")
    private ResponseDto getChecklistsByQuery(HttpServletRequest request, @RequestParam String filter,
                                                          @RequestParam(required = false) String mainName, @RequestParam(required = false) String subName){

        //I don't implement the logic not yet


        return ResponseDto.of(true, "조회 성공");
    }


    @ApiDocumentResponse
    @Operation(summary = "Get all member's checklist API", description = "해당 멤버의 체크리스트 모두 조회합니다.")
    @GetMapping("/api/members/checklists")
    private DataResponseDto<List<ChecklistPostDto>> getAllMemberChecklists(HttpServletRequest request){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get all member's checklist entity
        List<Checklist> memberChecklists = member.getChecklists();

        //entity convert to dto
        List<ChecklistPostDto> checklistPostDtos = new ArrayList<>();
        for (Checklist checklist : memberChecklists) {
            checklistPostDtos.add(ChecklistPostDto.createDto(checklist));
        }

        return DataResponseDto.of(checklistPostDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "[Test] Get all member's checklist in progress API", description = "해당 멤버의 진행중 체크리스트 모두 조회합니다.")
    @GetMapping("/api/members/checklists/progress")
    private DataResponseDto<List<ChecklistDetailDto>> getAllMemberChecklistInProgress(HttpServletRequest request){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get all member's checklist entity
        List<Checklist> memberChecklists = checklistService.getAllMemberChecklistsInProgress(member);

        //entity convert to dto
        List<ChecklistDetailDto> checklistDetailDtos = new ArrayList<>();
        for (Checklist checklist : memberChecklists) {
            checklistDetailDtos.add(ChecklistDetailDto.createDto(checklist));
        }

        return DataResponseDto.of(checklistDetailDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "[Test] Get all member's checklist in complete API", description = "해당 멤버의 완료한 체크리스트를 모두 조회합니다.")
    @GetMapping("/api/members/checklists/complete")
    private DataResponseDto<List<ChecklistPostDto>> getAllMemberChecklistInComplete(HttpServletRequest request){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get all member's checklist entity
        List<Checklist> memberChecklists = checklistService.getAllMemberChecklistsInComplete(member);

        //entity convert to dto
        List<ChecklistPostDto> checklistPostDtos = new ArrayList<>();
        for (Checklist checklist : memberChecklists) {
            checklistPostDtos.add(ChecklistPostDto.createDto(checklist));
        }

        return DataResponseDto.of(checklistPostDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "[Test] Get all member's checklist that is made by the member API", description = "해당 멤버가 만든 체크리스트를 모두 조회합니다.")
    @GetMapping("/api/members/checklists/owner")
    private DataResponseDto<List<ChecklistPostDto>> getAllMemberChecklistByMember(HttpServletRequest request){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get all member's checklist entity
        List<Checklist> memberChecklists = checklistService.getAllMemberChecklistsMadeByMember(member);

        //entity convert to dto
        List<ChecklistPostDto> checklistPostDtos = new ArrayList<>();
        for (Checklist checklist : memberChecklists) {
            checklistPostDtos.add(ChecklistPostDto.createDto(checklist));
        }

        return DataResponseDto.of(checklistPostDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "[Test] Get recommended checklist API", description = "회원의 관심 카테고리 기반으로 체크리스트를 추천합니다.\n\n")
    @GetMapping("/api/members/checklists/recommend")
    private DataResponseDto<List<ChecklistPostDto>> recommendChecklistByCategory(HttpServletRequest request){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        List<Checklist> checklists = checklistService.findByCategoryIn(member);

        List<ChecklistPostDto> checklistPostDtos = new ArrayList<>();
        for(Checklist checklist : checklists){
            checklistPostDtos.add(ChecklistPostDto.createDto(checklist));
        }

        //TODO : 만약 체크리스트 개수가 0개일 경우 세모체 스탠다드 체크리스트 반환하기.
        return DataResponseDto.of(checklistPostDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Get checklist's detail info by id API", description = "체크리스트 id를 통해 조회합니다.\n\n" +
            "해당 멤버 소유의 체크리스트만 접근 가능합니다.")
    @GetMapping("/api/members/checklists/{checklist_id}")
    private DataResponseDto<ChecklistDetailDto> getChecklistById(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);
        if(!checklist.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        return DataResponseDto.of(ChecklistDetailDto.createDto(checklist), "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Get checklist by id API (No Login)", description = "체크리스트 id를 통해 조회합니다. - 회원의 체크리스트가 아니더라도 조회 가능합니다.\n\n" +
            "해당 멤버 소유의 체크리스트만 접근 가능합니다.")
    @GetMapping("/api/checklists/{checklist_id}")
    private DataResponseDto<ChecklistPostDto> getChecklistByIdNoLogin(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);

        return DataResponseDto.of(ChecklistPostDto.createDto(checklist), "조회 성공");
    }

    //======= create method ======//
    @ApiDocumentResponse
    @Operation(summary = "Create new checklist API", description = "새로운 체크리스트를 생성합니다.\n\n" +
            "필수 목록 : [title]")
    @PostMapping(value = "/api/members/checklists", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    private DataResponseDto<Long> createNewChecklist(HttpServletRequest request, @RequestPart("request") CreateChecklistRequestDto requestDto, @RequestPart("image") MultipartFile imgFile){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Create New Checklist Entity
        Long checklistId = checklistService.createChecklist(requestDto, member, imgFile);

        return DataResponseDto.of(checklistId, "체크리스트 생성 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Use existed checklist API by checklist id", description = "기존에 존재하는 체크리스트를 사용합니다.\n\n" +
            "다른 사람의 체크리스트만 사용가능합니다.\n\n" +
            "자신의 체크리스트는 이미 사용중인 상태로 만약 checklist가 자기 소유일경우 에러를 발생합니다. - 400 Bad request")
    @PostMapping(value = "/api/members/checklists/{checklist_id}/use")
    private DataResponseDto<Long> useExistedChecklist(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);
        if(checklist.getOwner().equals(member)) throw new GeneralException(Code.BAD_REQUEST, "자신의 체크리스트는 이미 사용중인 상태입니다.");

        //Create checklist
        Long newChecklistId = checklistService.useChecklist(checklist, member);

        return DataResponseDto.of(newChecklistId, "체크리스트 생성 성공");
    }

    //======= update method ======//
    @ApiDocumentResponse
    @Operation(summary = "Update checklist's info API", description = "체크리스트 정보를 수정합니다.\n\n" +
            "회원의 체크리스트가 아닌 경우 수정이 불가능합니다. - 403 Forbidden error\n\n" +
            "수정하고 싶은 체크리스트 정보만 넘겨주시면 됩니다. 수정이 필요하지 않은 정보는 입력하지 않아도 됩니다.\n\n" +
            "===step list 관련 내용=== \n\n" +
            "step list 부분은 수정하든 안 하든 일단 포함해서 넘겨줘야 합니다.\n\n" +
            "수정을 하지 않더라도 기존 step id에 기존 정보를 포함시켜서 넘겨줘야 합니다.\n\n" +
            "step list 부분은 기존 정보를 넘기지 않을 시 삭제된 것으로 간주됩니다.\n\n" +
            "step id가 -1인 경우는 새로 추가된 step으로 간주됩니다.")
    @PostMapping("/api/members/checklists/{checklist_id}")
    private DataResponseDto<ChecklistPostDto> updateChecklistInfo(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId, @RequestPart("request") UpdateChecklistRequestDto requestDto,
                                                                  @RequestPart("image") MultipartFile imgFile){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);
        if(!checklist.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        //Update checklist's info Entity
        checklistService.updateChecklist(checklist, requestDto, imgFile);

        return DataResponseDto.of(ChecklistPostDto.createDto(checklist), "체크리스트 수정 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Update checklist progress API", description = "해당 체크리스트의 진행 정보를 수정합니다.\n\n" +
            "회원의 체크리스트가 아닌 경우 수정할 수 없습니다. - 403 Forbidden error\n\n" +
            "만약 해당 체크리스트의 없는 step id인 경우 에러를 발생합니다. - 404 Not found error\n\n" +
            "update할 step만 넣어주면 됩니다.")
    @PutMapping("/api/members/checklists/{checklist_id}/steps")
    private ResponseDto updateProgress(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId, @RequestBody UpdateStepRequestDto requestDto){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);
        if(!checklist.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        //Insert stepItem into checklist entity
        //TODO : order 중복에 따른 에러 처리 + 순차적으로 증가하게 설정
        checklistService.updateStepProgress(checklist, requestDto);

        return ResponseDto.of(true, "수정 성공");
    }

    //======= delete method ======//
    @ApiDocumentResponse
    @Operation(summary = "Delete a checklist API", description = "회원의 체크리스트를 삭제합니다.\n\n" +
            "회원의 체크리스트가 아닌 경우 삭제가 불가능합니다. - 403 Forbidden error")
    @DeleteMapping("/api/members/checklists/{checklist_id}")
    private ResponseDto deleteChecklist(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);
        if(!checklist.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        //Delete checklist
        checklistService.deleteChecklist(checklist, member);

        return ResponseDto.of(true, "삭제 성공");
    }
}
