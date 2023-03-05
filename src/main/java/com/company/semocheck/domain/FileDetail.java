package com.company.semocheck.domain;

import com.company.semocheck.utils.MultipartUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class FileDetail {

    @Id
    @Column(name="file_id")
    private String id;
    @NotNull
    private String folder;

    @NotNull
    private String name;

    @NotNull
    private String format;

    @NotNull
    private String path;

    private Long bytes;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public static FileDetail createEntity(String folder, MultipartFile multipartFile){
        final String fileId = MultipartUtil.createFileId();
        final String fileName = MultipartUtil.getFileName(multipartFile.getOriginalFilename());
        final String format = MultipartUtil.getFormat(multipartFile.getContentType());

        return FileDetail.builder()
                .id(fileId)
                .folder(folder)
                .name(fileName)
                .format(format)
                .path(MultipartUtil.createPath(folder, fileName, fileId, format))
                .bytes(multipartFile.getSize())
                .build();
    }

    public static FileDetail copyEntity(FileDetail fileDetail){
        final String fileId = MultipartUtil.createFileId();
        final String path = MultipartUtil.createPath(fileDetail.getFolder(), fileDetail.getName(), fileId, fileDetail.getFormat());

        return FileDetail.builder()
                .id(fileId)
                .folder(fileDetail.getFolder())
                .name(fileDetail.getName())
                .format(fileDetail.getFormat())
                .path(path)
                .bytes(fileDetail.getBytes())
                .build();
    }
}
