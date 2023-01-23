package com.company.semocheck.domain.dto.checklist;

import com.company.semocheck.domain.CheckList;
import com.company.semocheck.domain.Step;
import com.company.semocheck.domain.dto.FileDto;
import com.company.semocheck.domain.dto.StepDto;
import com.company.semocheck.domain.dto.SubCategoryDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CheckListDetailDto {

    private Long checkListId;
    private String ownerName;
    private Long originCheckListId;
    private SubCategoryDto category;
    private List<StepDto> stepItems = new ArrayList<>();
    private String title;
    private String brief;

    private Integer stepCount;
    private Integer viewCount;
    private Integer scrapCount;
    private Integer ageGroup;
    private Boolean visibility;

    private String createdDate;
    private String modifiedDate;

    private String progress;
    private FileDto fileDto;

    public static CheckListDetailDto createDto(CheckList checkList) {
        CheckListDetailDto dto = new CheckListDetailDto();
        dto.checkListId = checkList.getId();
        dto.ownerName = checkList.getOwner().getName();
        dto.title = checkList.getTitle();
        dto.brief = checkList.getBrief();
        dto.visibility = checkList.getVisibility();
        dto.progress = checkList.getProgress();

        dto.stepCount = checkList.getStepCount();
        dto.viewCount = checkList.getViewCount();
        dto.scrapCount = checkList.getScrapCount();
        dto.ageGroup = checkList.getAgeGroup();

        dto.createdDate = checkList.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        dto.modifiedDate = checkList.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        if(checkList.getOrigin() != null) dto.originCheckListId = checkList.getOrigin().getId();
        if(checkList.getCategory() != null) dto.category = SubCategoryDto.createDto(checkList.getCategory());
        if(checkList.getFileDetail() != null) dto.fileDto = FileDto.createDto(checkList.getFileDetail());

        for (Step step : checkList.getSteps()) {
            dto.stepItems.add(StepDto.createDto(step));
        }
        return dto;
    }
}
