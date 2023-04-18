package com.company.semocheck.domain.dto.checklist;

import com.company.semocheck.domain.checklist.Checklist;
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
public class ChecklistOwnerDetailDto {

    //Id
    private Long checklistId;
    private Long originChecklistId;

    //Info
    private String title;
    private String brief;
    private String ownerName;
    private SubCategoryDto category;
    private List<StepDto> steps = new ArrayList<>();
    private Integer stepCount;
    private Integer viewCount;
    private Integer scrapCount;
    private Float avgAge;
    private Boolean avgSex;
    private Boolean isCopied;

    //Image
    private FileDto image;
    private FileDto defaultImage;

    private String colorCode;

    //Date
    private String createdDate;
    private String modifiedDate;
    private String checkedDate;

    //detail info
    private Boolean publish;
    private Boolean complete;
    private Integer progress;


    public static ChecklistOwnerDetailDto createDto(Checklist checklist) {
        ChecklistOwnerDetailDto dto = new ChecklistOwnerDetailDto();

        //Id
        dto.checklistId = checklist.getId();
        dto.originChecklistId = checklist.getOrigin() != null ? checklist.getOrigin().getId() : null;

        //Info
        dto.title = checklist.getTitle();
        dto.brief = checklist.getBrief();
        dto.ownerName = checklist.getOwner().getName();
        dto.stepCount = checklist.getStepCount();
        dto.viewCount = checklist.getStatsInfo().getViewCount();
        dto.scrapCount = checklist.getStatsInfo().getScrapCount();
        dto.isCopied = checklist.getIsCopied();
        if(checklist.getCategory() != null) dto.category = SubCategoryDto.createDto(checklist.getCategory());
        if(checklist.getStatsInfo().getViewCount() >= 10){ // 조회수 10회 이상일 경우만
            dto.avgAge = checklist.getStatsInfo().getAvgAge();
            dto.avgSex = checklist.getStatsInfo().getViewCountFemale() > checklist.getStatsInfo().getViewCountMale();
        }

        //Detail Info
        dto.publish = checklist.getPublish();
        dto.complete = checklist.getUsageInfo().getComplete();
        dto.progress = checklist.getUsageInfo().getProgress();
        for (Step step : checklist.getSteps()) {
            dto.steps.add(StepDto.createDto(step));
        }
        dto.steps.sort(Comparator.comparing(StepDto::getOrder));

        //Image
        if(checklist.getImage() != null) dto.image = FileDto.createDto(checklist.getImage());
        if(checklist.getDefaultImage() != null) dto.defaultImage = FileDto.createDto(checklist.getDefaultImage());
        dto.colorCode = checklist.getColorCode();

        //Date
        dto.createdDate = checklist.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        dto.modifiedDate = checklist.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        dto.checkedDate = checklist.getUsageInfo().getCheckedDate() != null ? checklist.getUsageInfo().getCheckedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : null;

        return dto;
    }
}
