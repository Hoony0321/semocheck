package com.company.semocheck.controller;

import com.company.semocheck.common.response.ApiDocumentResponse;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.SubCategory;
import com.company.semocheck.domain.MainCategory;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import com.company.semocheck.domain.dto.request.category.CreateCategoryRequestDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "카테고리", description = "카테고리 관련 API 모음입니다.")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;


    //TODO : TEST 필요
    @ApiDocumentResponse
    @Operation(summary = "query categories API", description = "쿼리문을 통해 카테고리를 조회합니다.\n\n" +
            "default : 모든 카테고리 정보 출력\n\n" +
            "only main : 해당 1차 카테고리 정보(+하위 카테고리) 출력\n\n" +
            "only sub : 해당 이름을 가진 2차 카테고리들의 정보 출력\n\n" +
            "main / sub : 단일 카테고리 정보 출력" )
    @GetMapping("/api/categories")
    public DataResponseDto<List<SubCategoryDto>> getCategoriesByQuery(@RequestParam(required = false, name = "main") String mainName,
                                                                      @RequestParam(required = false, name = "sub") String subName){
        List<SubCategoryDto> CategoryDtos = new ArrayList<>();

        if(mainName == null && subName == null){ //모든 카테고리 출력
            for (SubCategory subCategory : categoryService.getAllSubCategories()) {
                SubCategoryDto dto = SubCategoryDto.createDto(subCategory);
                CategoryDtos.add(dto);
            }
        } else if(mainName != null && subName == null){ //해당 1차 카테고리 출력
            MainCategory mainCategory = categoryService.findMainCategoryByName(mainName);

            for (SubCategory subCategory : mainCategory.getSubCategoryList()) {
                SubCategoryDto dto = SubCategoryDto.createDto(subCategory);
                CategoryDtos.add(dto);
            }
        } else if (mainName == null && subName != null) { //해당 2차 카테고리 출력
            for (SubCategory subCategory : categoryService.findSubCategoryByOnlySubName(subName)) {
                SubCategoryDto dto = SubCategoryDto.createDto(subCategory);
                CategoryDtos.add(dto);
            }
        } else if (mainName != null && subName != null) { //단일 2차 카테고리 출력
            SubCategory subCategory = categoryService.findSubCategoryByName(mainName, subName);
            CategoryDtos.add(SubCategoryDto.createDto(subCategory));
        }


        return DataResponseDto.of(CategoryDtos, "조회 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Create categories API", description = "카테고리를 생성합니다.")
    @PostMapping("/api/categories")
    public ResponseDto createCategories(@RequestBody CreateCategoryRequestDto requestDto){
        categoryService.createCategory(requestDto);
        return ResponseDto.of(true, "카테고리 생성 성공");
    }

    @ApiDocumentResponse
    @Operation(summary = "Delete category API", description = "카테고리를 삭제합니다.\n\n" +
            "1차 카테고리가 삭제될 경우, 연결된 하위 카테고리가 존재하면 삭제가 안됩니다. -> BAD_REQUEST(400) 반환")
    @DeleteMapping("/api/categories")
    public ResponseDto deleteMainCategories(@RequestParam(required = false, name = "main") String mainName,
                                            @RequestParam(required = false, name = "sub") String subName){

        if(mainName == null) throw new GeneralException(Code.BAD_REQUEST);

        if(subName == null) categoryService.removeMainCategory(mainName);
        else categoryService.removeSubCategory(mainName, subName);

        return ResponseDto.of(true, "카테고리 삭제 성공");
    }

    //=== 추후에 category 정보 업데이트가 필요한 경우 사용 ===//
//    @ApiDocumentResponse
//    @Operation(summary = "Update sub categories API", description = "2차 카테고리 정보를 수정합니다.(name : 수정할 카테고리의 main 카테고리 이름)\n\n" +
//            "main category name / sub category name 둘 중 하나만 변경 가능합니다.\n" +
//            "둘 다 변경할 경우 -> remove 하시고 새로 create 하시면 됩니다.")
//    @PutMapping("/api/categories/{main_name}/sub/{sub_name}")
//    public ResponseDto updateSubCategories(@PathVariable("main_name") String mainName, @PathVariable("sub_name") String subName,
//                                           @RequestBody UpdateSubCategoryRequestDto requestDto){
//        if(requestDto.getMainName() != null && requestDto.getSubName() != null ) throw new GeneralException(Code.BAD_REQUEST, "두 값을 한번에 변경할 수 없습니다.");
//        categoryService.updateSubCategory(requestDto, mainName, subName);
//        return ResponseDto.of(true, "2차 카테고리 수정 성공");
//    }

}
