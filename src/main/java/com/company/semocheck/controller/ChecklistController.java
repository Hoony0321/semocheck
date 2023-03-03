package com.company.semocheck.controller;

import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.dto.checklist.ChecklistDetailDto;
import com.company.semocheck.domain.dto.checklist.ChecklistPostDto;
import com.company.semocheck.domain.dto.checklist.TempChecklistDto;
import com.company.semocheck.domain.request.checklist.CreateChecklistRequestDto;
import com.company.semocheck.domain.request.checklist.UpdateChecklistRequestDto;
import com.company.semocheck.domain.request.checklist.UpdateStepRequestDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.ChecklistService;
import com.company.semocheck.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name = "체크리스트", description = "체크리스트 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
public class ChecklistController {

    private final MemberService memberService;
    private final ChecklistService checklistService;


    //======= read method ======//
    @ApiDocumentResponse
    @Operation(summary = "Query checklist API by options", description = "쿼리문을 통해 체크리스트를 조회합니다.\n\n" +
            "[검색 옵션]\n\n" +
            "category_main : string\n\n" +
            "category_sub : string\n\n" +
            "title : string\n\n" +
            "owner : string\n\n" +
            "\n\n\n\n" +
            "[정렬 옵션]\n\n" +
            "sort : [date, view, scrap]\n\n" +
            "direction : [asc, desc]\n\n")
    @GetMapping("/api/checklists")
    private DataResponseDto<List<ChecklistPostDto>> getChecklistsByQuery(@RequestParam(name = "category_main", required = false) String categoryMain, @RequestParam(name = "category_sub", required = false) String categorySub,
                                                                         @RequestParam(required = false) String title, @RequestParam(required = false) String owner,
                                                                         @RequestParam(required = false, defaultValue = "date") String sort,
                                                                         @RequestParam(required = false, defaultValue = "desc") String direction){

        //get checklists by query
        List<Checklist> checklists = checklistService.getPublishedChecklistByQuery(categoryMain, categorySub, title, owner);

        //sorting checklists
        checklists = checklistService.sortChecklists(checklists, sort, direction);

        List<ChecklistPostDto> checklistPostDtos = new ArrayList<>();
        for(Checklist checklist : checklists){
            checklistPostDtos.add(ChecklistPostDto.createDto(checklist));
        }

        return DataResponseDto.of(checklistPostDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Query member's checklist API by options", description = "쿼리문을 통해 해당 회원의 체크리스트를 조회합니다.\n\n" +
            "[검색 옵션]\n\n" +
            "categoryMain : string\n\n" +
            "categorySub : string\n\n" +
            "title : string\n\n" +
            "published : boolean\n\n" +
            "completed : boolean\n\n" +
            "\n\n\n\n" +
            "[정렬옵션]\n\n" +
            "sort : [date, view, scrap]\n\n" +
            "direction : [asc, desc]\n\n")
    @GetMapping("/api/members/checklists")
    private DataResponseDto<List<ChecklistPostDto>> getMemberChecklistsByQuery(HttpServletRequest request, @RequestParam(name = "category_main", required = false) String categoryMain, @RequestParam(name = "category_sub", required = false) String categorySub,
                                                                               @RequestParam(required = false) String title,
                                                                               @RequestParam(required = false) Boolean published,
                                                                               @RequestParam(required = false) Boolean completed,
                                                                               @RequestParam(required = false) Boolean owner,
                                                                               @RequestParam(required = false, defaultValue = "date") String sort,
                                                                               @RequestParam(required = false, defaultValue = "desc") String direction){

        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //get checklists by query
        List<Checklist> checklists = checklistService.getMemberChecklistsByQuery(member, categoryMain, categorySub, title, published, completed, owner);

        //sorting checklists
        checklists = checklistService.sortChecklists(checklists, sort, direction);

        List<ChecklistPostDto> checklistPostDtos = new ArrayList<>();
        for(Checklist checklist : checklists){
            checklistPostDtos.add(ChecklistPostDto.createDto(checklist));
        }

        return DataResponseDto.of(checklistPostDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Get member's temporay checklist API", description = "회원의 임시 체크리스트를 조회합니다.\n\n" +
            "해당 멤버 소유의 체크리스트만 접근 가능합니다.")
    @GetMapping("/api/members/checklists/temp")
    private DataResponseDto<List<TempChecklistDto>> getMemberTempChecklists(HttpServletRequest request){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        List<Checklist> checklists = checklistService.getMemberTempChecklist(member);

        List<TempChecklistDto> tempChecklistDtos = new ArrayList<>();
        for(Checklist checklist : checklists){
            tempChecklistDtos.add(TempChecklistDto.createDto(checklist));
        }

        return DataResponseDto.of(tempChecklistDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Get simple info of checklist by id API (No Login)", description = "체크리스트 id를 통해 조회합니다. - 회원의 체크리스트가 아니더라도 조회 가능합니다.\n\n" +
            "해당 멤버 소유의 체크리스트만 접근 가능합니다.")
    @GetMapping("/api/checklists/{checklist_id}")
    private DataResponseDto<ChecklistPostDto> getChecklistByIdNoLogin(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);
        if(!checklist.getPublish()) throw new GeneralException(Code.NOT_FOUND, "해당 id의 체크리스트는 존재하지 않습니다.");

        return DataResponseDto.of(ChecklistPostDto.createDto(checklist), "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Get detail info of checklist by id API", description = "체크리스트 id를 통해 조회합니다.\n\n" +
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
    @Operation(summary = "Get recommended checklist API", description = "회원의 정보를 토대로 체크리스트를 추천합니다.\n\n")
    @GetMapping("/api/members/checklists/recommend")
    private DataResponseDto<List<ChecklistPostDto>> recommendChecklistByCategory(HttpServletRequest request){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        List<Checklist> checklists = checklistService.recommendChecklist(member);

        List<ChecklistPostDto> checklistPostDtos = new ArrayList<>();
        for(Checklist checklist : checklists){
            checklistPostDtos.add(ChecklistPostDto.createDto(checklist));
        }

        //TODO : 만약 체크리스트 개수가 0개일 경우 세모체 스탠다드 체크리스트 반환하기.
        return DataResponseDto.of(checklistPostDtos, "조회 성공");
    }





    //======= create method ======//
    @ApiDocumentResponse
    @Operation(summary = "Create new checklist API", description = "새로운 체크리스트를 생성합니다.\n\n" +
            "필수 목록 : [title]")
    @PostMapping(value = "/api/members/checklists")
    private DataResponseDto<ChecklistPostDto> createChecklist(HttpServletRequest request, @RequestBody CreateChecklistRequestDto requestDto){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Create a checklist Entity
        Long checklistId = checklistService.createChecklist(requestDto, member);

        //Get the checklist entity
        Checklist checklist = checklistService.findById(checklistId);

        return DataResponseDto.of(ChecklistPostDto.createDto(checklist), "체크리스트 생성 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Use existed checklist API by checklist id", description = "기존에 존재하는 체크리스트를 사용합니다.;\n\n" +
            "다른 사람의 체크리스트만 사용가능합니다.\n\n" +
            "자신의 체크리스트는 이미 사용중인 상태입니다.\n\n" +
            "따라서 만약 checklist가 자기 소유일경우 에러를 발생합니다. - 400 Bad request")
    @PostMapping(value = "/api/members/checklists/{checklist_id}")
    private DataResponseDto<Long> useExistedChecklist(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Optional<Checklist> findOne = member.getChecklists().stream().filter(chk -> chk.getId().equals(checklistId)).findFirst();
        if(findOne.isPresent()) throw new GeneralException(Code.BAD_REQUEST, "이미 사용중인 체크리스트입니다.");

        //Create checklist
        Checklist checklist = checklistService.findById(checklistId);
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
    @PutMapping("/api/members/checklists/{checklist_id}")
    private DataResponseDto<ChecklistPostDto> updateChecklistInfo(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId, @RequestBody UpdateChecklistRequestDto requestDto){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);
        if(!checklist.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        //Update checklist's info Entity
        checklistService.updateChecklist(checklist, requestDto);

        return DataResponseDto.of(ChecklistPostDto.createDto(checklist), "체크리스트 수정 성공");
    }



    @ApiDocumentResponse
    @Operation(summary = "Update checklist progress API", description = "해당 체크리스트의 진행 정보를 수정합니다.\n\n" +
            "회원의 체크리스트가 아닌 경우 수정할 수 없습니다. - 403 Forbidden error\n\n" +
            "만약 해당 체크리스트의 없는 step id인 경우 에러를 발생합니다. - 404 Not found error\n\n" +
            "update할 step만 넣어주면 됩니다.")
    @PatchMapping("/api/members/checklists/{checklist_id}/steps")
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