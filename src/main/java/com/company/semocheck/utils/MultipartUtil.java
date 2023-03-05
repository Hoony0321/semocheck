package com.company.semocheck.utils;

import org.springframework.util.StringUtils;

import java.util.UUID;

public class MultipartUtil {

    //새로운 파일 고유 ID 생성
    public static String createFileId(){
        return UUID.randomUUID().toString();
    }

    //Multipart ContentType 값에서 확장자 반환
    public static String getFormat(String contentType){
        if(StringUtils.hasText(contentType)){
            return contentType.substring((contentType.lastIndexOf('/') +1));
        }
        return null;
    }

    //파일 전체 경로 생성
    public static String  createPath(String folder, String fileName, String fileId, String format){
        return String.format("%s/%s_%s.%s", folder, fileName, fileId, format);
    }

    public static String getFileName(String originalFileName) {
        if(StringUtils.hasText(originalFileName)){
            return originalFileName.substring(0, originalFileName.indexOf('.'));
        }
        return null;
    }
}
