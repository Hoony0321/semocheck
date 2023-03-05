package com.company.semocheck.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.domain.FileDetail;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.FileDetailRepository;
import com.company.semocheck.utils.MultipartUtil;
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


    @Transactional
    public FileDetail upload(String folder, MultipartFile multipartFile) {
        //create file detail by given info
        FileDetail fileDetail = FileDetail.createEntity(folder, multipartFile);
        String fullPath = fileDetail.getPath();

        //create object metedata
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        //upload file to aws
        try(InputStream inputStream = multipartFile.getInputStream()){
            amazonS3Client.putObject(new PutObjectRequest(bucket, fullPath, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        }catch (IOException e){
            throw new GeneralException(Code.BAD_REQUEST, e.getMessage());
        }

        //file save
        save(fileDetail);
        return fileDetail;
    }

    @Transactional
    public FileDetail copy(FileDetail target){
        FileDetail fileDetail = FileDetail.copyEntity(target);
        try{
            amazonS3Client.copyObject(new CopyObjectRequest(bucket, target.getPath(), bucket, fileDetail.getPath()));
        }
        catch (Exception e){
            throw new GeneralException(Code.BAD_REQUEST, e.getMessage());
        }

        save(fileDetail);
        return fileDetail;
    }

    @Transactional
    public void save(FileDetail fileDetail){
        Optional<FileDetail> findOne = fileDetailRepository.findById(fileDetail.getId());
        if(findOne.isPresent()) throw new GeneralException(Code.NOT_FOUND, "이미 저장된 파일입니다.");

        try{
            fileDetailRepository.save(fileDetail);
        } catch (Exception e){
            //delete file on aws
            amazonS3Client.deleteObject(bucket, fileDetail.getPath());
            throw new GeneralException(Code.TRANSACTION_NOT_COMMITED, "트랜잭션 실패");
        }
    }

    @Transactional
    public void removeFile(FileDetail fileDetail){
        //delete file on aws
        amazonS3Client.deleteObject(bucket, fileDetail.getPath());
        fileDetailRepository.delete(fileDetail);
    }

    public FileDetail findById(String id) {
        Optional<FileDetail> findOne = fileDetailRepository.findById(id);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, "해당 번호의 파일은 존재하지 않습니다.");

        return findOne.get();
    }

}
