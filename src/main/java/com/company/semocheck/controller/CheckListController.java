package com.company.semocheck.controller;

import com.company.semocheck.auth.jwt.JwtUtils;
import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.CheckList;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.dto.CheckListDto;
import com.company.semocheck.domain.dto.request.checkList.CreateCheckListRequestDto;
import com.company.semocheck.domain.dto.request.checkList.CreateStepRequestDto;
import com.company.semocheck.domain.dto.request.checkList.UpdateCheckListRequestDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.CheckListService;
import com.company.semocheck.service.MemberService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "체크리스트", description = "체크리스트 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{member_id}/checkList")
public class CheckListController {

    private final MemberService memberService;
    private final CheckListService checkListService;
    private final JwtUtils jwtUtils;

    @ApiDocumentResponse
    @Operation(summary = "Create new checklist API", description = "새로운 체크리스트를 생성합니다.\n\n" +
            "필수 목록 : [title]")
    @PostMapping("")
    private DataResponseDto<Long> createNewCheckList(HttpServletRequest request, @PathVariable("member_id") Long memberId,
                                                     @RequestBody CreateCheckListRequestDto requestDto){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Create New CheckList Entity
        Long checkListId = checkListService.createCheckList(requestDto, member);

        return DataResponseDto.of(checkListId, "체크리스트 생성 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "get all member's checklist API", description = "해당 멤버의 체크리스트 모두 조회합니다.")
    @GetMapping("")
    private DataResponseDto<List<CheckListDto>> findAllMemberCheckList(HttpServletRequest request, @PathVariable("member_id") Long memberId){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Get all member's checkList entity
        List<CheckListDto> checkListDtos = new ArrayList<>();
        for (CheckList checkList : member.getCheckLists()) {
            checkListDtos.add(CheckListDto.createDto(checkList));
        }

        return DataResponseDto.of(checkListDtos, "조회 성공");
    }



    @ApiDocumentResponse
    @Operation(summary = "get checklist by id API", description = "체크리스트 id를 통해 조회합니다.\n\n" +
            "해당 멤버 소유의 체크리스트만 접근 가능합니다.")
    @GetMapping("/{checkList_id}")
    private DataResponseDto<CheckListDto> findCheckListById(HttpServletRequest request, @PathVariable("member_id") Long memberId,
                                                            @PathVariable("checkList_id") Long checkListId){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Get checkList by id
        CheckList checkList = checkListService.findById(checkListId);
        if(!checkList.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        return DataResponseDto.of(CheckListDto.createDto(checkList), "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Update checklist's info API", description = "체크리스트 정보를 수정합니다.\n\n" +
            "\"회원의 체크리스트가 아닌 경우 수정이 불가능합니다. - 403 Forbidden error\"" +
            "step 추가 api가 아닙니다. step 추가 api는 따로 있습니다.\n" +
            "수정하고 싶은 칼럼만 넘겨주시면 됩니다. 수정이 필요하지 않은 정보는 입력하지 않아도 됩니다.")
    @PutMapping("/{checkList_id}")
    private DataResponseDto<CheckListDto> updateCheckListInfo(HttpServletRequest request, @PathVariable("member_id") Long memberId,
                                                              @PathVariable("checkList_id") Long checkListId, @RequestBody UpdateCheckListRequestDto requestDto){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Get checkList by id
        CheckList checkList = checkListService.findById(checkListId);
        if(!checkList.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        //Update checkList's info Entity
        checkListService.updateCheckList(requestDto, checkList);

        return DataResponseDto.of(CheckListDto.createDto(checkList), "체크리스트 수정 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Insert step item into checkList API", description = "해당 체크리스트에 stepItem을 추가합니다.\n\n" +
            "\"회원의 체크리스트가 아닌 경우 추가할 수 없습니다. - 403 Forbidden error\"")
    @PostMapping("/{checkList_id}/stepItem")
    private DataResponseDto<CheckListDto> AddStepItem(HttpServletRequest request, @PathVariable("member_id") Long memberId,
                                                      @PathVariable("checkList_id") Long checkListId, @RequestBody CreateStepRequestDto requestDto){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Get checkList by id
        CheckList checkList = checkListService.findById(checkListId);
        if(!checkList.getOwner().equals(member)) throw new GeneralException(Code.FORBIDDEN);

        //Insert stepItem into checkList entity
        checkListService.addStepItem(requestDto, checkList);

        return DataResponseDto.of(CheckListDto.createDto(checkList), "체크리스트 수정 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Delete a checklist API", description = "회원의 체크리스트를 삭제합니다.\n\n" +
            "회원의 체크리스트가 아닌 경우 삭제가 불가능합니다. - 403 Forbidden error")
    @DeleteMapping("/{checkList_id}")
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
        checkListService.removeCheckList(checkList);

        return ResponseDto.of(true, "삭제 성공");
    }
}
