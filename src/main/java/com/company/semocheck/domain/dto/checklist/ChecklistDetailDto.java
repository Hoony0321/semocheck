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
import java.util.List;

@Data
@NoArgsConstructor
public class ChecklistDetailDto {

    private Long checklistId;
    private String ownerName;
    private Long originChecklistId;
    private SubCategoryDto category;
    private List<StepDto> stepItems = new ArrayList<>();
    private String title;
    private String brief;

    private Integer stepCount;
    private Integer viewCount;
    private Integer scrapCount;
    private Integer ageGroup;
    private Boolean publish;

    private String createdDate;
    private String modifiedDate;

    private Boolean complete;
    private String progress;
    private FileDto fileDto;

    public static ChecklistDetailDto createDto(Checklist checklist) {
        ChecklistDetailDto dto = new ChecklistDetailDto();
        dto.checklistId = checklist.getId();
        dto.ownerName = checklist.getOwner().getName();
        dto.title = checklist.getTitle();
        dto.brief = checklist.getBrief();
        dto.publish = checklist.getPublish();
        dto.complete = checklist.getComplete();
        dto.progress = checklist.getProgress();

        dto.stepCount = checklist.getStepCount();
        dto.viewCount = checklist.getViewCount();
        dto.scrapCount = checklist.getScrapCount();
        dto.ageGroup = checklist.getAgeGroup();

        dto.createdDate = checklist.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        dto.modifiedDate = checklist.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        if(checklist.getOrigin() != null) dto.originChecklistId = checklist.getOrigin().getId();
        if(checklist.getCategory() != null) dto.category = SubCategoryDto.createDto(checklist.getCategory());
        if(checklist.getFileDetail() != null) dto.fileDto = FileDto.createDto(checklist.getFileDetail());

        for (Step step : checklist.getSteps()) {
            dto.stepItems.add(StepDto.createDto(step));
        }
        return dto;
    }
}
