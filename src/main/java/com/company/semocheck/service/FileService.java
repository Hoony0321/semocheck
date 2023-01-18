package com.company.semocheck.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.domain.FileDetail;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.FileDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
@ConditionalOnProperty(prefix = "cloud.aws.s3", name = "bucket")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final AmazonS3Client amazonS3Client;
    private final FileDetailRepository fileDetailRepository;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    //TODO : 현재는 AWS 저장되고 Entity 저장되는 구조임 -> 엔티티 생성 과정에서 오류가 생기면 S3에 잉여 파일이 올라가게 됨. -> 수정 필요
    public FileDetail upload(String folder, MultipartFile multipartFile) {
        FileDetail fileDetail = FileDetail.createEntity(folder, multipartFile);
        String fullPath = fileDetail.getPath();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        try(InputStream inputStream = multipartFile.getInputStream()){
            amazonS3Client.putObject(new PutObjectRequest(bucket, fullPath, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        }catch (IOException e){
            throw new GeneralException(Code.BAD_REQUEST, e.getMessage());
        }

        return fileDetail;
    }

    @Transactional
    public String save(FileDetail fileDetail){
        Optional<FileDetail> findOne = fileDetailRepository.findById(fileDetail.getId());
        if(findOne.isPresent()) throw new GeneralException(Code.NOT_FOUND, "이미 저장된 파일입니다.");
        fileDetailRepository.save(fileDetail);
        return fileDetail.getId();
    }

    public FileDetail findById(String id) {
        Optional<FileDetail> findOne = fileDetailRepository.findById(id);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, "해당 번호의 파일은 존재하지 않습니다.");

        return findOne.get();
    }
}
