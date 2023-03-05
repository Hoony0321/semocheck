package com.company.semocheck.domain.dto.checklist;

import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.Step;
import com.company.semocheck.domain.dto.FileDto;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import com.company.semocheck.domain.dto.step.StepPostDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@NoArgsConstructor
public class ChecklistSimpleDto {

    //Id
    protected Long checklistId;

    //Info
    protected String title;
    protected SubCategoryDto category;

    //Image
    protected Integer defaultImage;
    protected FileDto fileDto;

    //Count
    protected Integer viewCount;
    protected Integer stepCount;
    protected Integer scrapCount;

    //Average
    protected Float avgAge;
    protected Boolean avgSex;

    //Date
    protected String createdDate;
    protected String modifiedDate;

    static public ChecklistSimpleDto createDto(Checklist checklist) {
        ChecklistSimpleDto dto = new ChecklistSimpleDto();
        dto.checklistId = checklist.getId();
        dto.title = checklist.getTitle();
        dto.defaultImage = checklist.getDefaultImage();

        dto.stepCount = checklist.getStepCount();
        dto.viewCount = checklist.getViewCount();
        dto.scrapCount = checklist.getScrapCount();

        dto.createdDate = checklist.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        dto.modifiedDate = checklist.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        if(checklist.getCategory() != null) dto.category = SubCategoryDto.createDto(checklist.getCategory());
        if(checklist.getFileDetail() != null) dto.fileDto = FileDto.createDto(checklist.getFileDetail());

        if(dto.viewCount >= 10){ // 조회수 10회 이상일 경우만
            dto.avgAge = checklist.getAvgAge();
            dto.avgSex = checklist.getViewCountFemale() > checklist.getViewCountMale();
        }

        return dto;
    }
}
