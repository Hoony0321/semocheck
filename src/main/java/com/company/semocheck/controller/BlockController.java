package com.company.semocheck.controller;

import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.checklist.Block;
import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.dto.SearchResultDto;
import com.company.semocheck.domain.dto.checklist.BlockDto;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.request.DeleteBlocksRequest;
import com.company.semocheck.domain.request.member.CreateBlocksRequest;
import com.company.semocheck.service.MemberService;
import com.company.semocheck.service.checklist.BlockService;
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
public class BlockController {

    private final BlockService blockService;
    private final MemberService memberService;
    private final ChecklistService checklistService;


    @ApiDocumentResponse
    @Operation(summary = "차단리스트 조회 API", description = "회원의 차단리스트를 조회합니다.\n\n")
    @GetMapping("/api/members/blocks")
    private DataResponseDto<SearchResultDto> getBlocks(HttpServletRequest request){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get blocklists by member
        List<Block> blocklists = blockService.getBlocks(member);

        //Return blocklists to searchResults
        List<BlockDto> blockDtos = new ArrayList<>();
        for(Block block : blocklists){
            blockDtos.add(BlockDto.createDto(block));
        }

        return DataResponseDto.of(SearchResultDto.createDto(blockDtos), Code.SUCCESS_READ);
    }

    @ApiDocumentResponse
    @Operation(summary = "차단리스트 추가 API", description = "회원의 차단리스트에 추가합니다.\n\n" +
            "id 리스트 형식으로 차단리스트에 새로운 체크리스트를 추가할 수 있습니다.\n\n" +
            "차단리스트에 추가하려는 체크리스트가 이미 차단리스트에 존재하는 경우, 이미 차단된 체크리스트입니다. 라는 에러메시지를 반환합니다.\n\n" +
            "차단리스트에 추가하려는 체크리스트가 존재하지 않는 경우, 존재하지 않는 체크리스트입니다. 라는 에러메시지를 반환합니다.\n\n")
    @PostMapping("/api/members/blocks")
    private ResponseDto addBlocks(HttpServletRequest request, @RequestBody CreateBlocksRequest createBlocksRequest){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        createBlocksRequest.getChecklistIds().forEach(checklistId -> {
            //Get checklist by id
            Checklist checklist = checklistService.findById(checklistId);

            //Insert stepItem into checklist entity
            blockService.createBlock(member, checklist);
        });

        return ResponseDto.of(true, Code.SUCCESS_CREATE);
    }

    @ApiDocumentResponse
    @Operation(summary = "차단리스트 추가 API", description = "회원의 차단리스트에 추가합니다.\n\n")
    @PostMapping("/api/members/blocks/{checklist_id}")
    private ResponseDto addBlock(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);

        //Insert stepItem into checklist entity
        blockService.createBlock(member, checklist);

        return ResponseDto.of(true, Code.SUCCESS_CREATE);
    }

    @ApiDocumentResponse
    @Operation(summary = "차단리스트 삭제 API", description = "회원의 차단리스트에서 삭제합니다.\n\n" +
            "id 리스트 형식으로 차단리스트에 새로운 체크리스트를 삭제할 수 있습니다.\n\n" +
            "차단리스트에 삭제하려는 체크리스트가 이미 차단리스트에 존재하지 않는 경우, 존재하지 않는 체크리스트입니다. 라는 에러메시지를 반환합니다.\n\n")
    @DeleteMapping("/api/members/blocks")
    private ResponseDto deleteBlocks(HttpServletRequest request, @RequestBody DeleteBlocksRequest deleteBlocksRequest){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        deleteBlocksRequest.getChecklistIds().forEach(checklistId -> {
            //Get checklist by id
            Checklist checklist = checklistService.findById(checklistId);

            //Insert stepItem into checklist entity
            blockService.deleteBlock(member, checklist);
        });

        return ResponseDto.of(true, Code.SUCCESS_DELETE);
    }



    @ApiDocumentResponse
    @Operation(summary = "차단리스트 삭제 API", description = "회원의 차단리스트에서 삭제합니다.\n\n")
    @DeleteMapping("/api/members/blocks/{checklist_id}")
    private ResponseDto deleteBlock(HttpServletRequest request, @PathVariable("checklist_id") Long checklistId){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Get checklist by id
        Checklist checklist = checklistService.findById(checklistId);

        //Insert stepItem into checklist entity
        blockService.deleteBlock(member, checklist);

        return ResponseDto.of(true, Code.SUCCESS_DELETE);
    }
}
