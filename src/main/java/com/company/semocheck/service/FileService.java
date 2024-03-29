package com.company.semocheck.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.domain.FileDetail;
import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.dto.FileDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.FileDetailRepository;
import com.company.semocheck.service.checklist.ChecklistService;
import com.company.semocheck.utils.MultipartUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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
            throw new GeneralException(Code.BAD_REQUEST);
        }

        //file save
        save(fileDetail);
        return fileDetail;
    }

    @Transactional
    public FileDetail copy(FileDetail target){
        FileDetail fileDetail = FileDetail.copyEntity(target);
        try{
            amazonS3Client.copyObject(new CopyObjectRequest(bucket, target.getPath(), bucket, fileDetail.getPath()).withCannedAccessControlList(CannedAccessControlList.PublicRead));
        }
        catch (Exception e){
            throw new GeneralException(Code.BAD_REQUEST);
        }

        save(fileDetail);
        return fileDetail;
    }

    @Transactional
    public void save(FileDetail fileDetail){
        Optional<FileDetail> findOne = fileDetailRepository.findById(fileDetail.getId());
        if(findOne.isPresent()) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.EXISTED_FILE);

        try{
            fileDetailRepository.save(fileDetail);
        } catch (Exception e){
            //delete file on aws
            amazonS3Client.deleteObject(bucket, fileDetail.getPath());
            throw new GeneralException(Code.TRANSACTION_NOT_COMMITED);
        }
    }

    @Transactional
    public void removeFile(FileDetail fileDetail){
        //delete file on aws
        amazonS3Client.deleteObject(bucket, fileDetail.getPath());
        //delete entity on db
        fileDetailRepository.delete(fileDetail);
    }

    public FileDetail findById(String id) {
        Optional<FileDetail> findOne = fileDetailRepository.findById(id);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_FILE);

        return findOne.get();
    }

    public List<FileDetail> findByFolder(String folder){
        List<FileDetail> fileDetails = fileDetailRepository.findAllByFolder(folder);
        return fileDetails;
    }

    public FileDetail findDocsByName(String name){
        List<FileDetail> docs = fileDetailRepository.findAllByFolder("docs");

        Optional<FileDetail> findOne = docs.stream().filter(doc -> doc.getName().equals(name)).findFirst();
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_FILE);

        return findOne.get();
    }

}
