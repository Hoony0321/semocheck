package com.company.semocheck.controller;

import com.company.semocheck.auth.jwt.JwtUtils;
import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.CheckList;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.dto.checklist.CheckListDetailDto;
import com.company.semocheck.domain.dto.checklist.CheckListPostDto;
import com.company.semocheck.domain.dto.request.checkList.CreateCheckListRequestDto;
import com.company.semocheck.domain.dto.request.checkList.UpdateCheckListRequestDto;
import com.company.semocheck.domain.dto.request.checkList.UpdateStepRequestDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.CategoryService;
import com.company.semocheck.service.CheckListService;
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
public class CheckListController {

    private final MemberService memberService;
    private final CheckListService checkListService;

    private final CategoryService categoryService;
    private final JwtUtils jwtUtils;


    //======= read method ======//
    @ApiDocumentResponse
    @Operation(summary = "Get all visibile checkList API", description = "공개된 모든 체크리스트를 조회합니다.\n\n")
    @GetMapping("/api/checkList")
    private DataResponseDto<List<CheckListPostDto>> getAllVisibleCheckLists(HttpServletRequest request){
        List<CheckList> checkLists = checkListService.getAllVisibleCheckLists();

        //entity convert to dto
        List<CheckListPostDto> checkListPostDtos = new ArrayList<>();
        for (CheckList checkList : checkLists) {
            checkListPostDtos.add(CheckListPostDto.createDto(checkList));
        }

        return DataResponseDto.of(checkListPostDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "[Test] Query checkList API", description = "쿼리문을 통해 체크리스트를 조회합니다.(테스트중)\n\n")
    @GetMapping("/api/checkList/query")
    private ResponseDto getAllVisibleCheckListsByCategory(HttpServletRequest request, @RequestParam String filter,
                                                          @RequestParam(required = false) String mainName, @RequestParam(required = false) String subName){
        System.out.println(filter + mainName + subName);
        return ResponseDto.of(true, "조회 성공");
    }


    @ApiDocumentResponse
    @Operation(summary = "Get all member's checklist API", description = "해당 멤버의 체크리스트 모두 조회합니다.")
    @GetMapping("/api/members/{member_id}/checkList")
    private DataResponseDto<List<CheckListPostDto>> getAllMemberCheckLists(HttpServletRequest request, @PathVariable("member_id") Long memberId){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Get all member's checkList entity
        List<CheckList> memberCheckLists = member.getCheckLists();

        //entity convert to dto
        List<CheckListPostDto> checkListPostDtos = new ArrayList<>();
        for (CheckList checkList : memberCheckLists) {
            checkListPostDtos.add(CheckListPostDto.createDto(checkList));
        }

        return DataResponseDto.of(checkListPostDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "[Test] Get all member's checklist in progress API", description = "해당 멤버의 진행중 체크리스트 모두 조회합니다.")
    @GetMapping("/api/members/{member_id}/checkList/progress")
    private DataResponseDto<List<CheckListDetailDto>> getAllMemberCheckListInProgress(HttpServletRequest request, @PathVariable("member_id") Long memberId){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Get all member's checkList entity
        List<CheckList> memberCheckLists = checkListService.getAllMemberCheckListsInProgress(member);

        //entity convert to dto
        List<CheckListDetailDto> checkListDetailDtos = new ArrayList<>();
        for (CheckList checkList : memberCheckLists) {
            checkListDetailDtos.add(CheckListDetailDto.createDto(checkList));
        }

        return DataResponseDto.of(checkListDetailDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "[Test] Get all member's checklist in complete API", description = "해당 멤버의 완료한 체크리스트를 모두 조회합니다.")
    @GetMapping("/api/members/{member_id}/checkList/complete")
    private DataResponseDto<List<CheckListPostDto>> getAllMemberCheckListInComplete(HttpServletRequest request, @PathVariable("member_id") Long memberId){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Get all member's checkList entity
        List<CheckList> memberCheckLists = checkListService.getAllMemberCheckListsInComplete(member);

        //entity convert to dto
        List<CheckListPostDto> checkListPostDtos = new ArrayList<>();
        for (CheckList checkList : memberCheckLists) {
            checkListPostDtos.add(CheckListPostDto.createDto(checkList));
        }

        return DataResponseDto.of(checkListPostDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "[Test] Get all member's checklist that is made by the member API", description = "해당 멤버가 만든 체크리스트를 모두 조회합니다.")
    @GetMapping("/api/members/{member_id}/checkList/owner")
    private DataResponseDto<List<CheckListPostDto>> getAllMemberCheckListByMember(HttpServletRequest request, @PathVariable("member_id") Long memberId){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Get all member's checkList entity
        List<CheckList> memberCheckLists = checkListService.getAllMemberCheckListsMadeByMember(member);

        //entity convert to dto
        List<CheckListPostDto> checkListPostDtos = new ArrayList<>();
        for (CheckList checkList : memberCheckLists) {
            checkListPostDtos.add(CheckListPostDto.createDto(checkList));
        }

        return DataResponseDto.of(checkListPostDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Get checklist's detail info by id API", description = "체크리스트 id를 통해 조회합니다.\n\n" +
            "해당 멤버 소유의 체크리스트만 접근 가능합니다.")
    @GetMapping("/api/members/{member_id}/checkList/{checkList_id}")
    private DataResponseDto<CheckListDetailDto> getCheckListById(HttpServletRequest request, @PathVariable("member_id") Long memberId,
                                                                  @PathVariable("checkList_id") Long checkListId){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Get checkList by id
        CheckList checkList = checkListService.findById(checkListId);
        if(!checkList.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        return DataResponseDto.of(CheckListDetailDto.createDto(checkList), "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Get checklist by id API (No Login)", description = "체크리스트 id를 통해 조회합니다. - 회원의 체크리스트가 아니더라도 조회 가능합니다.\n\n" +
            "해당 멤버 소유의 체크리스트만 접근 가능합니다.")
    @GetMapping("/api/checkList/{checkList_id}")
    private DataResponseDto<CheckListPostDto> getCheckListByIdNoLogin(HttpServletRequest request, @PathVariable("checkList_id") Long checkListId){
        //Get checkList by id
        CheckList checkList = checkListService.findById(checkListId);

        return DataResponseDto.of(CheckListPostDto.createDto(checkList), "조회 성공");
    }

    //======= create method ======//
    @ApiDocumentResponse
    @Operation(summary = "Create new checklist API", description = "새로운 체크리스트를 생성합니다.\n\n" +
            "필수 목록 : [title]")
    @PostMapping(value = "/api/members/{member_id}/checkList", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    private DataResponseDto<Long> createNewCheckList(HttpServletRequest request, @PathVariable("member_id") Long memberId,
                                                     @RequestPart("request") CreateCheckListRequestDto requestDto, @RequestPart("image") MultipartFile imgFile){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Create New CheckList Entity
        Long checkListId = checkListService.createCheckList(requestDto, member, imgFile);

        return DataResponseDto.of(checkListId, "체크리스트 생성 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Use existed checklist API by checkList id", description = "기존에 존재하는 체크리스트를 사용합니다.\n\n" +
            "다른 사람의 체크리스트만 사용가능합니다.\n\n" +
            "자신의 체크리스트는 이미 사용중인 상태로 만약 checkList가 자기 소유일경우 에러를 발생합니다. - 400 Bad request")
    @PostMapping(value = "/api/members/{member_id}/checkList/{checkList_id}/use")
    private DataResponseDto<Long> useExistedCheckList(HttpServletRequest request, @PathVariable("member_id") Long memberId, @PathVariable("checkList_id") Long checkListId){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Get checkList by id
        CheckList checkList = checkListService.findById(checkListId);
        if(checkList.getOwner().equals(member)) throw new GeneralException(Code.BAD_REQUEST, "자신의 체크리스트는 이미 사용중인 상태입니다.");

        //Create checkList
        Long newCheckListId = checkListService.useCheckList(checkList, member);

        return DataResponseDto.of(newCheckListId, "체크리스트 생성 성공");
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
    @PostMapping("/api/members/{member_id}/checkList/{checkList_id}")
    private DataResponseDto<CheckListPostDto> updateCheckListInfo(HttpServletRequest request, @PathVariable("member_id") Long memberId,
                                                                  @PathVariable("checkList_id") Long checkListId, @RequestPart("request") UpdateCheckListRequestDto requestDto,
                                                                  @RequestPart("image") MultipartFile imgFile){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Get checkList by id
        CheckList checkList = checkListService.findById(checkListId);
        if(!checkList.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        //Update checkList's info Entity
        checkListService.updateCheckList(checkList, requestDto, imgFile);

        return DataResponseDto.of(CheckListPostDto.createDto(checkList), "체크리스트 수정 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Update checkList progress API", description = "해당 체크리스트의 진행 정보를 수정합니다.\n\n" +
            "회원의 체크리스트가 아닌 경우 수정할 수 없습니다. - 403 Forbidden error\n\n" +
            "만약 해당 체크리스트의 없는 step id인 경우 에러를 발생합니다. - 404 Not found error\n\n" +
            "update할 step만 넣어주면 됩니다.")
    @PutMapping("/api/members/{member_id}/checkList/{checkList_id}/steps")
    private ResponseDto updateProgress(HttpServletRequest request, @PathVariable("member_id") Long memberId,
                                    @PathVariable("checkList_id") Long checkListId, @RequestBody UpdateStepRequestDto requestDto){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Get checkList by id
        CheckList checkList = checkListService.findById(checkListId);
        if(!checkList.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        //Insert stepItem into checkList entity
        //TODO : order 중복에 따른 에러 처리 + 순차적으로 증가하게 설정
        checkListService.updateStepProgress(checkList, requestDto);

        return ResponseDto.of(true, "수정 성공");
    }

    //======= delete method ======//
    @ApiDocumentResponse
    @Operation(summary = "Delete a checklist API", description = "회원의 체크리스트를 삭제합니다.\n\n" +
            "회원의 체크리스트가 아닌 경우 삭제가 불가능합니다. - 403 Forbidden error")
    @DeleteMapping("/api/members/{member_id}/checkList/{checkList_id}")
    private ResponseDto deleteCheckList(HttpServletRequest request, @PathVariable("member_id") Long memberId,
                                        @PathVariable("checkList_id") Long checkListId){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Get checkList by id
        CheckList checkList = checkListService.findById(checkListId);
        if(!checkList.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        //Delete checkList
        checkListService.deleteCheckList(checkList, member);

        return ResponseDto.of(true, "삭제 성공");
    }
}
