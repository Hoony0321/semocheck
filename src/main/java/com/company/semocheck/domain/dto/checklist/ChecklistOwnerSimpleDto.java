package com.company.semocheck.domain.dto.checklist;

import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.dto.FileDto;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class ChecklistOwnerSimpleDto {

    //Id
    private Long checklistId;

    //Info
    private String title;
    private SubCategoryDto category;
    private Integer stepCount;
    private Float avgAge;
    private Boolean avgSex;
    private Integer progress;
    private Boolean isCopied;

    //Image
    private FileDto image;
    private FileDto defaultImage;
    private String colorCode;

    //Date
    private String createdDate;
    private String modifiedDate;
    private String checkedDate;

    static public ChecklistOwnerSimpleDto createDto(Checklist checklist) {
        ChecklistOwnerSimpleDto dto = new ChecklistOwnerSimpleDto();
        //Id
        dto.checklistId = checklist.getId();

        //Info
        dto.title = checklist.getTitle();
        dto.stepCount = checklist.getStepCount();
        dto.isCopied = checklist.getIsCopied();
        dto.progress = checklist.getUsageInfo().getProgress();
        if(checklist.getCategory() != null) dto.category = SubCategoryDto.createDto(checklist.getCategory());
        if(checklist.getStatsInfo().getViewCount() >= 10){ // 조회수 10회 이상일 경우만
            dto.avgAge = checklist.getStatsInfo().getAvgAge();
            dto.avgSex = checklist.getStatsInfo().getViewCountFemale() > checklist.getStatsInfo().getViewCountMale();
        }

        //Image
        if(checklist.getImage() != null) dto.image = FileDto.createDto(checklist.getImage());
        if(checklist.getDefaultImage() != null) dto.defaultImage = FileDto.createDto(checklist.getDefaultImage());
        dto.colorCode = checklist.getColorCode();

        //Date
        dto.createdDate = checklist.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        dto.modifiedDate = checklist.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        dto.checkedDate = checklist.getUsageInfo().getCheckedDate() != null ?
                checklist.getUsageInfo().getCheckedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : null;

        return dto;
    }
}
