package com.company.semocheck.controller;

import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ResponseDto;
import com.company.semocheck.domain.FileDetail;
import com.company.semocheck.service.CategoryService;
import com.company.semocheck.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "파일", description = "파일 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final CategoryService categoryService;

    @Operation(summary = "Upload file to main category API", description = "1차 카테고리의 이미지 사진을 업로드합니다.")
    @PostMapping("/api/categories/{name}/files")
    public ResponseDto uploadFile_MainCategory(@RequestPart("image")MultipartFile multipartFile, @PathVariable("name") String name){

        FileDetail fileDetail = fileService.upload("category/image", multipartFile);
        categoryService.setMainCategoryFile(name, fileDetail);

        return ResponseDto.of(true, "파일 업로드 성공");
    }

    @Operation(summary = "Upload file to sub category API", description = "2차 카테고리의 이미지 사진을 업로드합니다.")
    @PostMapping("/api/categories/{mainName}/sub/{subName}/files")
    public ResponseDto uploadFile_SubCategory(@RequestPart("image")MultipartFile multipartFile, @PathVariable("mainName") String mainName,
                                               @PathVariable("subName") String subName){

        FileDetail fileDetail = fileService.upload("category/image", multipartFile);
        categoryService.setSubCategoryFile(mainName, subName, fileDetail);

        return ResponseDto.of(true, "파일 업로드 성공");
    }

    @Operation(summary = "Get a file's detail infomation API", description = "해당 번호에 해당하는 파일에 대한 자세한 정보를 조회합니다.")
    @GetMapping("/api/files/{id}")
    public DataResponseDto<FileDetail> getFileDetil(@PathVariable("id") String id){
        FileDetail file = fileService.findById(id);
        return DataResponseDto.of(file, "조회 성공");
    }



}
