package com.company.semocheck.domain.dto.checklist;

import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.Step;
import com.company.semocheck.domain.dto.FileDto;
import com.company.semocheck.domain.dto.step.StepDto;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@NoArgsConstructor
public class ChecklistDetailDto extends ChecklistSimpleDto {

    //post info
    private String ownerName;
    private Long originChecklistId;
    private String brief;
    private List<StepDto> steps = new ArrayList<>();

    //detail info
    private Boolean publish;
    private Boolean complete;
    private String progress;


    public static ChecklistDetailDto createDto(Checklist checklist) {
        ChecklistDetailDto dto = new ChecklistDetailDto();
        dto.checklistId = checklist.getId();
        dto.ownerName = checklist.getOwner().getName();
        dto.title = checklist.getTitle();
        dto.brief = checklist.getBrief();
        dto.publish = checklist.getPublish();
        dto.complete = checklist.getComplete();
        dto.progress = checklist.getProgress();
        dto.defaultImage = checklist.getDefaultImage();

        dto.stepCount = checklist.getStepCount();
        dto.viewCount = checklist.getViewCount();
        dto.scrapCount = checklist.getScrapCount();

        dto.createdDate = checklist.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        dto.modifiedDate = checklist.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        if(checklist.getOrigin() != null) dto.originChecklistId = checklist.getOrigin().getId();
        if(checklist.getCategory() != null) dto.category = SubCategoryDto.createDto(checklist.getCategory());
        if(checklist.getFileDetail() != null) dto.fileDto = FileDto.createDto(checklist.getFileDetail());

        for (Step step : checklist.getSteps()) {
            dto.steps.add(StepDto.createDto(step));
        }

        if(dto.viewCount >= 10){ // 조회수 10회 이상일 경우만
            dto.avgAge = checklist.getAvgAge();
            dto.avgSex = checklist.getViewCountFemale() > checklist.getViewCountMale();
        }

        dto.steps.sort(Comparator.comparing(StepDto::getOrder));
        return dto;
    }
}
