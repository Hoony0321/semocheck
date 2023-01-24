package com.company.semocheck.controller;

import com.company.semocheck.auth.jwt.JwtUtils;
import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.CheckList;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.dto.ScrapDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.CheckListService;
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

    private final JwtUtils jwtUtils;
    private final ScrapService scrapService;
    private final MemberService memberService;
    private final CheckListService checkListService;

    @ApiDocumentResponse
    @Operation(summary = "Scrap checkList API", description = "해당 체크리스트를 스크랩합니다.\n\n" +
            "회원의 체크리스트인 경우 스크랩할 수 없습니다. - 400 Bad request error\n\n")
    @PostMapping("/api/members/{member_id}/scraps/{checkList_id}")
    private ResponseDto createScrap(HttpServletRequest request, @PathVariable("member_id") Long memberId, @PathVariable("checkList_id") Long checkListId){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Get checkList by id
        CheckList checkList = checkListService.findById(checkListId);
        if(checkList.getOwner().equals(member)) throw new GeneralException(Code.BAD_REQUEST, "자신의 체크리스트를 스크랩할 수 없습니다.");

        //Insert stepItem into checkList entity
        scrapService.createScrap(member, checkList);

        return ResponseDto.of(true, "스크랩 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Get member's scrap list API", description = "회원의 스크랩 리스트를 조회합니다.\n\n")
    @GetMapping("/api/members/{member_id}/scraps")
    private DataResponseDto<List<ScrapDto>> getScraps(HttpServletRequest request, @PathVariable("member_id") Long memberId){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Insert stepItem into checkList entity
        List<ScrapDto> dtos = scrapService.getScrap(member);

        return DataResponseDto.of(dtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Delete member's scrap API", description = "회원의 스크랩을 checkList Id를 통해 삭제합니다.\n\n")
    @DeleteMapping("/api/members/{member_id}/scraps/{checkList_id}")
    private ResponseDto deleteScrap(HttpServletRequest request, @PathVariable("member_id") Long memberId, @PathVariable("checkList_id") Long checkListId){
        //JWT Member 검증
        String accessToken = jwtUtils.getAccessToken(request);
        Claims claims = jwtUtils.parseClaims(accessToken);
        Member member = memberService.findByOAuthIdAndProvider((String) claims.get("oAuthId"), (String) claims.get("provider"));
        if(!member.getId().equals(memberId)) throw new GeneralException(Code.FORBIDDEN);

        //Get checkList by id
        CheckList checkList = checkListService.findById(checkListId);

        //Insert stepItem into checkList entity
        scrapService.deleteScrap(member, checkList);

        return ResponseDto.of(true, "삭제 성공");
    }
}
