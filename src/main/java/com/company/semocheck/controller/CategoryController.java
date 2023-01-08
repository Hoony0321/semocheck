package com.company.semocheck.controller;

import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.SubCategory;
import com.company.semocheck.domain.MainCategory;
import com.company.semocheck.domain.dto.MainCategoryDto;
import com.company.semocheck.domain.dto.SubCategoryDto;
import com.company.semocheck.domain.dto.request.category.CreateMainCategoryRequestDto;
import com.company.semocheck.domain.dto.request.category.CreateSubCategoryRequestDto;
import com.company.semocheck.domain.dto.request.category.UpdateMainCategoryRequestDto;
import com.company.semocheck.domain.dto.request.category.UpdateSubCategoryRequestDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "카테고리", description = "카테고리 관련 API 모음입니다.")
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @ApiDocumentResponse
    @Operation(summary = "Get all main categories API", description = "1차 카테고리(대카테고리)를 전부 조회합니다.")
    @GetMapping("")
    public DataResponseDto<List<MainCategoryDto>> getAllMainCategories(){
        List<MainCategoryDto> mainCategoryDtos = new ArrayList<>();
        for (MainCategory category : categoryService.getAllMainCategories()) {
            MainCategoryDto dto = MainCategoryDto.createDto(category);
            mainCategoryDtos.add(dto);
        }
        return DataResponseDto.of(mainCategoryDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Create main categories API", description = "1차 카테고리를 생성합니다.")
    @PostMapping("")
    public ResponseDto createMainCategories(@RequestBody CreateMainCategoryRequestDto requestDto){
        categoryService.createMainCategory(requestDto);
        return ResponseDto.of(true, "1차 카테고리 생성 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Update main categories API", description = "1차 카테고리 정보를 수정합니다.")
    @PutMapping("/{name}")
    public ResponseDto updateMainCategories(@PathVariable("name") String name, @RequestBody UpdateMainCategoryRequestDto requestDto){
        categoryService.updateMainCategory(requestDto, name);
        return ResponseDto.of(true, "1차 카테고리 수정 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Delete main categories API", description = "1차 카테고리를 삭제합니다.\n" +
            "연결된 하위 카테고리가 존재할 경우 삭제가 안됩니다. -> BAD_REQUEST(400) 반환")
    @DeleteMapping("/{name}")
    public ResponseDto deleteMainCategories(@PathVariable("name") String name){
        categoryService.removeMainCategory(name);
        return ResponseDto.of(true, "1차 카테고리 삭제 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Get all categories by using main category's name API", description = "1차 카테고리의 하위 2차 카테고리를 전부 조회합니다.")
    @GetMapping("/{name}/sub")
    public DataResponseDto<List<SubCategoryDto>> getSubCategoriesByMainCategory(@PathVariable("name") String name){
        MainCategory mainCategory = categoryService.findMainCategoryByName(name);

        List<SubCategoryDto> CategoryDtos = new ArrayList<>();
        for (SubCategory subCategory : mainCategory.getSubCategoryList()) {
            SubCategoryDto dto = SubCategoryDto.createDto(subCategory);
            CategoryDtos.add(dto);
        }

        return DataResponseDto.of(CategoryDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Create sub categories API", description = "2차 카테고리를 생성합니다.")
    @PostMapping("/{name}/sub")
    public ResponseDto createSubCategories(@PathVariable("name") String name, @RequestBody CreateSubCategoryRequestDto requestDto){
        categoryService.createSubCategory(requestDto, name);
        return ResponseDto.of(true, "2차 카테고리 생성 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Update sub categories API", description = "2차 카테고리 정보를 수정합니다.(name : 수정할 카테고리의 main 카테고리 이름)\n" +
            "main category name / sub category name 둘 중 하나만 변경 가능합니다.\n" +
            "둘 다 변경할 경우 -> remove 하시고 새로 create 하시면 됩니다.")
    @PutMapping("/{mainName}/sub/{subName}")
    public ResponseDto updateSubCategories(@PathVariable("mainName") String mainName, @PathVariable("subName") String subName,
                                           @RequestBody UpdateSubCategoryRequestDto requestDto){
        if(requestDto.getMainName() != null && requestDto.getSubName() != null ) throw new GeneralException(Code.BAD_REQUEST, "두 값을 한번에 변경할 수 없습니다.");
        categoryService.updateSubCategory(requestDto, mainName, subName);
        return ResponseDto.of(true, "2차 카테고리 수정 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Delete sub categories API", description = "2차 카테고리를 삭제합니다.")
    @DeleteMapping("/{mainName}/sub/{subName}")
    public ResponseDto deleteSubCategories(@PathVariable("mainName") String mainName, @PathVariable("subName") String subName){
        categoryService.removeSubCategory(mainName, subName);
        return ResponseDto.of(true, "2차 카테고리 삭제 성공");
    }








    @ApiDocumentResponse
    @Operation(summary = "Get all sub categories API", description = "2차 카테고리(소카테고리)를 전부 조회합니다. -> 필요할까요...?")
    @GetMapping("/sub")
    public DataResponseDto<List<SubCategoryDto>> getAllSubCategories(){
        List<SubCategoryDto> CategoryDtos = new ArrayList<>();
        for (SubCategory subCategory : categoryService.getAllSubCategories()) {
            SubCategoryDto dto = SubCategoryDto.createDto(subCategory);
            CategoryDtos.add(dto);
        }
        return DataResponseDto.of(CategoryDtos, "조회 성공");
    }

}
