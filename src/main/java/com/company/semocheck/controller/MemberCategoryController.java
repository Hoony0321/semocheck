package com.company.semocheck.controller;

import com.company.semocheck.auth.jwt.JwtUtils;
import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.SubCategory;
import com.company.semocheck.domain.dto.category.MemberCategoryDto;
import com.company.semocheck.domain.request.memberCategory.CreateMemberCategoryRequest;
import com.company.semocheck.domain.request.memberCategory.DeleteMemberCategoryRequest;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.CategoryService;
import com.company.semocheck.service.MemberService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "회원 카테고리", description = "회원 카테고리 관련 API 모음입니다.")
@RequiredArgsConstructor
public class MemberCategoryController {

    private final JwtUtils jwtUtils;
    private final MemberService memberService;
    private final CategoryService categoryService;

    @ApiDocumentResponse
    @Operation(summary = "Get member's categories API", description = "멤버 관심 카테고리 리스트를 조회합니다.")
    @GetMapping("/api/members/categories")
    public DataResponseDto<List<MemberCategoryDto>> getMemberCategories(HttpServletRequest request){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        List<MemberCategoryDto> categories = memberService.getCategories(member);

        return DataResponseDto.of(categories, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Add new member's category by category name API", description = "멤버 관심 카테고리 리스트에 새로운 카테고리를 추가합니다.")
    @PostMapping("/api/members/categories")
    public ResponseDto addMemberCategory(HttpServletRequest request, @RequestBody CreateMemberCategoryRequest requestDto){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        SubCategory category = categoryService.findSubCategoryByName(requestDto.getMainName(), requestDto.getSubName());

        memberService.addMemberCategory(member, category);
        return ResponseDto.of(true, "카테고리 추가 성공");
    }
    
    @ApiDocumentResponse
    @Operation(summary = "Delete member's category by category name API", description = "멤버 관심 카테고리 리스트에서 해당 이름의 카테고리를 삭제합니다.")
    @DeleteMapping("/api/members/categories")
    public ResponseDto deleteMemberCategory(HttpServletRequest request, @RequestBody DeleteMemberCategoryRequest requestDto){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        SubCategory category = categoryService.findSubCategoryByName(requestDto.getMainName(), requestDto.getSubName());

        memberService.deleteMemberCategory(member, category);
        return ResponseDto.of(true, "카테고리 삭제 성공");
    }


}
