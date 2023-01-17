package com.company.semocheck.domain.dto;

import com.company.semocheck.domain.FileDetail;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class FileDto {

    private String id;
    private String name;
    private String path;

    @Builder
    public FileDto(String id, String fileName, String path) {
        this.id = id;
        this.name = fileName;
        this.path = path;
    }

    public static FileDto createDto(FileDetail fileDetail){
        if(fileDetail == null) return null; //FileDetail이 존재하지 않을 경우 null로 반환

        final String awsLink = "https://semocheck-s3.s3.ap-northeast-2.amazonaws.com/";

        FileDto dto = new FileDto();
        dto.id = fileDetail.getId();
        dto.name = fileDetail.getName();

        //S3 위치 주소를 실제 주소로 변환하여 반환
        dto.path = awsLink + fileDetail.getPath();

        return dto;
    }
}