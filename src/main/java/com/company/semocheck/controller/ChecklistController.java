package com.company.semocheck.controller;

import com.company.semocheck.common.response.*;
import com.company.semocheck.domain.checklist.Block;
import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.dto.SearchResultDto;
import com.company.semocheck.domain.dto.checklist.*;
import com.company.semocheck.domain.request.checklist.CreateChecklistRequest;
import com.company.semocheck.domain.request.checklist.UpdateChecklistRequestDto;
import com.company.semocheck.domain.request.checklist.UpdateStepRequestDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.checklist.ChecklistService;
import com.company.semocheck.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public DataResponseDto<SearchResultDto<ChecklistPostSimpleDto>> queryChecklists(@RequestParam(name = "category_main", required = false) String categoryMain, @RequestParam(name = "category_sub", required = false) String categorySub,
                                                                                         @RequestParam(required = false) String title, @RequestParam(required = false) String owner,
                                                                                         @RequestParam(required = false, defaultValue = "date") String sort,
                                                                                         @RequestParam(required = false, defaultValue = "desc") String direction,
                                                                                         HttpServletRequest request){
        //Get member by jwt token
        Optional<Member> findOne = memberService.getMemberByJwtNoError(request);

        //get checklists by query
        List<Checklist> checklists = checklistService.queryPublishedChecklist(categoryMain, categorySub, title, owner);

        //block checklist if it is in blocklist
        if(findOne.isPresent()){
            List<Checklist> blocklist = findOne.get().getBlocks().stream().map(Block::getChecklist).collect(Collectors.toList());
            checklists = checklists.stream().filter(chk -> !blocklist.contains(chk)).collect(Collectors.toList());
        }

        //sorting checklists
        checklists = checklistService.sortChecklists(checklists, sort, direction);

        List<ChecklistPostSimpleDto> checklistPostSimpleDtos = new ArrayList<>();
        for(Checklist checklist : checklists){
            checklistPostSimpleDtos.add(ChecklistPostSimpleDto.createDto(checklist, findOne));
        }

        return DataResponseDto.of(SearchResultDto.createDto(checklistPostSimpleDtos), Code.SUCCESS_READ);
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
    public DataResponseDto<SearchResultDto<ChecklistPostSimpleDto>> queryMemberChecklists(HttpServletRequest request, @RequestParam(name = "category_main", required = false) String categoryMain, @RequestParam(name = "category_sub", required = false) String categorySub,
                                                                                               @RequestParam(required = false) String title,
                                                                                               @RequestParam(required = false) Boolean published,
                                                                                               @RequestParam(required = false) Boolean completed,
                                                                                               @RequestParam(required = false) Boolean owner,
                                                                                               @RequestParam(required = false, defaultValue = "date") String sort,
                                                                                               @RequestParam(required = false, defaultValue = "desc") String direction){

        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //get checklists by query
        List<Checklist> checklists = checklistService.queryMemberChecklists(member, categoryMain, categorySub, title, published, completed, owner);

        //sorting checklists
        checklists = checklistService.sortChecklists(checklists, sort, direction);

        List<ChecklistOwnerSimpleDto> checklistOwnerSimpleDtos = new ArrayList<>();
        for(Checklist checklist : checklists){
            checklistOwnerSimpleDtos.add(ChecklistOwnerSimpleDto.createDto(checklist));
        }

        return DataResponseDto.of(SearchResultDto.createDto(checklistOwnerSimpleDtos), Code.SUCCESS_READ);
    }

    @ApiDocumentResponse
    @Operation(summary = "Get checklist by id API (Not need login)", description = "체크리스트 id를 통해 조회합니다.\n\n" +
            "회원의 체크리스트가 아니더라도 조회 가능합니다.")
    @GetMapping("/api/checklists/{checklist_id}")
    public DataResponseDto<ChecklistPostDetailDto> getChecklistById(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //get member by jwt token
        Optional<Member> findOne = memberService.getMemberByJwtNoError(request);

        //get checklist by id
        Checklist checklist = checklistService.getPublishedChecklistById(checklistId);

        //modify checklist's avgAge & avgSex
        checklistService.updateChecklistStatsByViewer(checklist, findOne);

        //create dto
        ChecklistPostDetailDto dto = ChecklistPostDetailDto.createDto(checklist, findOne);

        return DataResponseDto.of(dto, Code.SUCCESS_READ);
    }

    @ApiDocumentResponse
    @Operation(summary = "Get checklist by id API", description = "체크리스트 id를 통해 조회합니다.\n\n" +
            "해당 멤버 소유의 체크리스트만 접근 가능합니다.")
    @GetMapping("/api/members/checklists/{checklist_id}")
    public DataResponseDto<ChecklistOwnerDetailDto> getMemberChecklistById(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);
        if(!checklist.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        return DataResponseDto.of(ChecklistOwnerDetailDto.createDto(checklist), Code.SUCCESS_READ);
    }

    @ApiDocumentResponse
    @Operation(summary = "Get recommended checklist API", description = "회원의 정보를 토대로 체크리스트를 추천합니다.\n\n")
    @GetMapping("/api/members/checklists/recommend")
    public DataResponseDto<SearchResultDto<ChecklistPostSimpleDto>> getRecommendChecklistByCategory(HttpServletRequest request){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        List<Checklist> checklists = checklistService.getRecommendChecklist(member);

        List<ChecklistPostSimpleDto> checklistPostSimpleDtos = new ArrayList<>();
        for(Checklist checklist : checklists){
            checklistPostSimpleDtos.add(ChecklistPostSimpleDto.createDto(checklist, Optional.of(member)));
        }

        //TODO : 만약 체크리스트 개수가 0개일 경우 세모체 스탠다드 체크리스트 반환하기.
        return DataResponseDto.of(SearchResultDto.createDto(checklistPostSimpleDtos), Code.SUCCESS_READ);
    }

    @ApiDocumentResponse
    @Operation(summary = "Get popular checklist API", description = "인기 체크리스트를 반환합니다.\n\n" +
            "조회수 순으로 10개의 체크리스트가 반환됩니다.\n\n" +
            "category filter를 적용할 수 있습니다. -> categoryMain, categorySub 입력하면 해당 카테고리에서 인기 체크리스트 반환됨.")
    @GetMapping("/api/checklists/popular")
    public DataResponseDto<SearchResultDto<ChecklistPostSimpleDto>> getPopularChecklists(@RequestParam(name = "categroy_main", required = false) String categoryMain,
                                                                                         @RequestParam(name = "category_sub", required = false) String categorySub){
        List<Checklist> checklists = checklistService.getPopularChecklist(categoryMain, categorySub);

        List<ChecklistPostSimpleDto> checklistPostSimpleDtos = new ArrayList<>();
        for(Checklist checklist : checklists){
            checklistPostSimpleDtos.add(ChecklistPostSimpleDto.createDto(checklist, Optional.empty()));
        }

        return DataResponseDto.of(SearchResultDto.createDto(checklistPostSimpleDtos), Code.SUCCESS_READ);
    }

    @ApiDocumentResponse
    @Operation(summary = "Get similar checklist API", description = "유사한 체크리스트를 반환합니다.\n\n" +
            "유사도 순으로 5개의 체크리스트가 반환됩니다.")
    @GetMapping("/api/checklists/{checklist_id}/similar")
    public DataResponseDto<SearchResultDto<ChecklistPostSimpleDto>> getSimilarChecklists(@PathVariable("checklist_id") Long checklistId){
        Checklist target = checklistService.findById(checklistId);
        List<Checklist> checklists = checklistService.getSimilarChecklist(target);

        List<ChecklistPostSimpleDto> checklistPostSimpleDtos = new ArrayList<>();
        for(Checklist checklist : checklists){
            checklistPostSimpleDtos.add(ChecklistPostSimpleDto.createDto(checklist, Optional.empty()));
        }

        return DataResponseDto.of(SearchResultDto.createDto(checklistPostSimpleDtos), Code.SUCCESS_READ);
    }

    @ApiDocumentResponse
    @Operation(summary = "Get home content API", description = "홈 화면에 노출될 체크리스트를 반환합니다.\n\n" +
            "카테고리 2개가 반환됩니다.")
    @GetMapping("/api/checklists/home")
    public DataResponseDto<SearchResultDto> getHomeContent(HttpServletRequest request){
        //Get member by jwt token
        Optional<Member> member = memberService.getMemberByJwtNoError(request);
        List<HomeChecklistDto> homeContent = checklistService.getHomeContent(member);


        return DataResponseDto.of(SearchResultDto.createDto(homeContent), Code.SUCCESS_READ);
    }



    //======= create method ======//
    @ApiDocumentResponse
    @Operation(summary = "Create new checklist API", description = "새로운 체크리스트를 생성합니다.\n\n" +
            "필수 목록 : [title]")
    @PostMapping(value = "/api/members/checklists")
    private ResponseDto createChecklist(HttpServletRequest request, @RequestBody CreateChecklistRequest requestDto){
        // get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        // validate requestDto
        requestDto.validate();

        // create a checklist Entity
        Long checklistId = checklistService.createChecklist(requestDto, member);

        return DataResponseDto.of(checklistId, Code.SUCCESS_CREATE);
    }

    @ApiDocumentResponse
    @Operation(summary = "Use existed checklist API by checklist id", description = "기존에 존재하는 체크리스트를 사용합니다.;\n\n" +
            "다른 사람의 체크리스트만 사용가능합니다.\n\n" +
            "자신의 체크리스트는 이미 사용중인 상태입니다.\n\n" +
            "따라서 만약 checklist가 자기 소유일경우 에러를 발생합니다. - 400 Bad request")
    @PostMapping(value = "/api/members/checklists/{checklist_id}/use")
    private DataResponseDto<Long> useExistedChecklist(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Create checklist
        Checklist checklist = checklistService.findById(checklistId);
        Long newChecklistId = checklistService.useChecklist(checklist, member);

        return DataResponseDto.of(newChecklistId, Code.SUCCESS_CREATE);
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
    private ResponseDto updateChecklistInfo(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId, @RequestBody UpdateChecklistRequestDto requestDto){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);
        if(!checklist.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        //Update checklist's info Entity
        checklistService.updateChecklist(checklist, requestDto);

        return ResponseDto.of(true, Code.SUCCESS_UPDATE);
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

        return ResponseDto.of(true, Code.SUCCESS_UPDATE);
    }

    @ApiDocumentResponse
    @Operation(summary = "Restart checklist progress API", description = "해당 체크리스트의 진행 정보를 초기화합니다.\n\n" +
            "회원의 체크리스트가 아닌 경우 수정할 수 없습니다. - 403 Forbidden error")
    @PutMapping("/api/members/checklists/{checklist_id}/restart")
    private ResponseDto restartProgress(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);
        if(!checklist.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        //Restart checklist's progress
        checklistService.restartProgress(checklist);

        return ResponseDto.of(true, Code.SUCCESS_UPDATE);
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

        return ResponseDto.of(true, Code.SUCCESS_DELETE);
    }
}
