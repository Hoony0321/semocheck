package com.company.semocheck.controller;

import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.FileDetail;
import com.company.semocheck.domain.MainCategory;
import com.company.semocheck.service.CategoryService;
import com.company.semocheck.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final CategoryService categoryService;

    @Operation(summary = "Upload file to main category API", description = "1차 카테고리의 이미지 사진을 업로드합니다.")
    @PostMapping("/api/categories/{name}/files")
    public ResponseDto uploadFile_MainCategory(@RequestPart("file")MultipartFile multipartFile, @PathVariable("name") String name){

        FileDetail fileDetail = fileService.save("category/image", multipartFile);
        categoryService.setMainCategoryFile(name, fileDetail);

        return ResponseDto.of(true, "파일 업로드 성공");
    }

    @Operation(summary = "Upload file to sub category API", description = "2차 카테고리의 이미지 사진을 업로드합니다.")
    @PostMapping("/api/categories/{mainName}/sub/{subName}/files")
    public ResponseDto uploadFile_SubCategory(@RequestPart("file")MultipartFile multipartFile, @PathVariable("mainName") String mainName,
                                               @PathVariable("subName") String subName){

        FileDetail fileDetail = fileService.save("category/image", multipartFile);
        categoryService.setSubCategoryFile(mainName, subName, fileDetail);

        return ResponseDto.of(true, "파일 업로드 성공");
    }


}
