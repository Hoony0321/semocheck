package com.company.semocheck.controller;

import com.company.semocheck.auth.jwt.JwtUtils;
import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.category.SubCategory;
import com.company.semocheck.domain.dto.category.MemberCategoryDto;
import com.company.semocheck.domain.request.memberCategory.CreateMemberCategoryRequest;
import com.company.semocheck.domain.request.memberCategory.DeleteMemberCategoryRequest;
import com.company.semocheck.domain.request.memberCategory.UpdateMemberCategoryRequest;
import com.company.semocheck.service.CategoryService;
import com.company.semocheck.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

        return DataResponseDto.of(categories, Code.SUCCESS_READ);
    }

    @ApiDocumentResponse
    @Operation(summary = "add member's categories by category name API", description = "멤버 관심 카테고리 리스트에 새로운 카테고리를 추가합니다.")
    @PostMapping("/api/members/categories")
    public ResponseDto addMemberCategories(HttpServletRequest request, @RequestBody CreateMemberCategoryRequest requestDto){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Add categories to memberCategory
        List<SubCategory> categories = new ArrayList<>();
        for(MemberCategoryDto categoryDto : requestDto.getCategories()){
            SubCategory category = categoryService.findSubCategoryByName(categoryDto.getMain(), categoryDto.getName());
            categories.add(category);
        }
        memberService.addMemberCategory(member, categories);

        return ResponseDto.of(true, Code.SUCCESS_ADD);
    }

    @ApiDocumentResponse
    @Operation(summary = "update member's category by category name API", description = "멤버 관심 카테고리를 수정합니다.")
    @PutMapping("/api/members/categories")
    public ResponseDto updateMemberCategory(HttpServletRequest request, @RequestBody UpdateMemberCategoryRequest requestDto){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Update categories to memberCategory
        List<SubCategory> categories = new ArrayList<>();
        for(MemberCategoryDto categoryDto : requestDto.getCategories()){
            SubCategory category = categoryService.findSubCategoryByName(categoryDto.getMain(), categoryDto.getName());
            categories.add(category);
        }
        memberService.updateMemberCategory(member, categories);

        return ResponseDto.of(true, Code.SUCCESS_UPDATE);
    }
    
    @ApiDocumentResponse
    @Operation(summary = "Delete member's category by category name API", description = "멤버 관심 카테고리 리스트에서 해당 이름의 카테고리를 삭제합니다.")
    @DeleteMapping("/api/members/categories")
    public ResponseDto deleteMemberCategory(HttpServletRequest request, @RequestBody DeleteMemberCategoryRequest requestDto){
        //Get member by jwt token
        Member member = memberService.getMemberByJwt(request);

        //Delete categories to memberCategory
        List<SubCategory> categories = new ArrayList<>();
        for(MemberCategoryDto categoryDto : requestDto.getCategories()){
            SubCategory category = categoryService.findSubCategoryByName(categoryDto.getMain(), categoryDto.getName());
            categories.add(category);
        }
        memberService.deleteMemberCategory(member, categories);

        return ResponseDto.of(true, Code.SUCCESS_DELETE);
    }


}
