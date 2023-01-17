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
import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "cloud.aws.s3", name = "bucket")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final AmazonS3Client amazonS3Client;
    private final FileDetailRepository fileDetailRepository;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    @Transactional
    public FileDetail save(String folder, MultipartFile multipartFile) {
        FileDetail fileDetail = FileDetail.createEntity(folder, multipartFile);
        String fullPath = fileDetail.getPath();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        try(InputStream inputStream = multipartFile.getInputStream()){
            amazonS3Client.putObject(new PutObjectRequest(bucket, fullPath, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            fileDetailRepository.save(fileDetail);
        }catch (IOException e){
            throw new GeneralException(Code.BAD_REQUEST, e.getMessage());
        }

        return fileDetail;
    }

}
