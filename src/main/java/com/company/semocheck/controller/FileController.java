package com.company.semocheck.controller;

import com.company.semocheck.common.response.*;
import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.FileDetail;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.dto.FileDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.CategoryService;
import com.company.semocheck.service.FileService;
import com.company.semocheck.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Tag(name = "파일", description = "파일 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    private final List<String> availableObjects = Arrays.asList("checklists", "members", "reports");

    @Operation(summary = "Get a file's detail info API", description = "해당 번호에 해당하는 파일에 대한 자세한 정보를 조회합니다.")
    @GetMapping("/api/files/{id}")
    public DataResponseDto<FileDetail> getFileDetil(@PathVariable("id") String id){
        FileDetail file = fileService.findById(id);
        return DataResponseDto.of(file, Code.SUCCESS_READ);
    }

    @ApiDocumentResponse
    @Operation(summary = "upload file api", description = "파일을 업로드합니다.")
    @PostMapping("/api/files")
    private DataResponseDto<FileDto> uploadFile(@RequestParam(required = false) String object, @RequestParam(value = "file", required = false) MultipartFile file) {
        //check validation
        if (file == null || file.isEmpty()) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.NOT_FOUND_FILE);
        if (object == null) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.NOT_FOUND_OBJECT);
        if(!availableObjects.contains(object)) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.INVALID_OBJECT);

        //file upload
        String location = String.format("%s/files", object);
        FileDetail fileDetail = fileService.upload(location, file);

        return DataResponseDto.of(FileDto.createDto(fileDetail));
    }

    @ApiDocumentResponse
    @Operation(summary = "upload category default image file api", description = "카테고리 디폴트 이미지 파일을 업로드합니다.")
    @PostMapping("/api/files/categories")
    private DataResponseDto<FileDto> uploadDefaultImageFile(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam(value = "main") String mainCategoryName, @RequestParam(value = "sub") String subCategoryName) {
        //check validation
        if (file == null || file.isEmpty()) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.NOT_FOUND_FILE);
        if (mainCategoryName == null || subCategoryName == null) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.NOT_FOUND_OBJECT);

        //file upload
        String location = String.format("categories/%s/%s", mainCategoryName, subCategoryName);
        FileDetail fileDetail = fileService.upload(location, file);

        return DataResponseDto.of(FileDto.createDto(fileDetail));
    }

    @ApiDocumentResponse
    @Operation(summary = "delete file api", description = "파일을 삭제합니다.")
    @DeleteMapping("/api/files/{id}")
    private ResponseDto deleteFile(@PathVariable("id") String id){
        FileDetail fileDetail = fileService.findById(id);
        fileService.removeFile(fileDetail);

        return ResponseDto.of(true, Code.SUCCESS_DELETE);
    }




}
