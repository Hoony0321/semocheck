package com.company.semocheck.controller;

import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.domain.FileDetail;
import com.company.semocheck.domain.dto.FileDto;
import com.company.semocheck.domain.dto.SearchResultDto;
import com.company.semocheck.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "문서", description = "문서 관련 API 모음입니다.")
@RestController
@RequiredArgsConstructor
public class DocsController {
    private final FileService fileService;

    @Operation(summary = "get docs file api", description = "문서 파일을 조회합니다.")
    @GetMapping("/api/docs")
    public DataResponseDto<SearchResultDto> getDocs() {
        List<FileDetail> fileDetails = fileService.findByFolder("docs");

        List<FileDto> fileDtos = new ArrayList<>();
        for(FileDetail fileDetail : fileDetails){
            fileDtos.add(FileDto.createDto(fileDetail));
        }

        return DataResponseDto.of(SearchResultDto.createDto(fileDtos));
    }

    @Operation(summary = "get docs file by name api", description = "문서 파일을 이름으로 조회합니다.")
    @GetMapping("/api/docs/{name}")
    public DataResponseDto<FileDto> getDocsByName(@PathVariable String name) {
        FileDetail fileDetail = fileService.findDocsByName(name);
        return DataResponseDto.of(FileDto.createDto(fileDetail));
    }
}
