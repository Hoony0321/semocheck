package com.company.semocheck.domain.dto;

import com.company.semocheck.domain.CheckList;
import com.company.semocheck.domain.StepItem;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CheckListDto {

    private Long checkListId;
    private String ownerName;

    private Long originCheckListId;
    private SubCategoryDto category;
    private List<StepDto> stepItems = new ArrayList<>();
    private String title;
    private String subTitle;
    private String brief;

    private Integer stepCount;
    private Integer viewCount;
    private Integer scrapCount;
    private Integer progress;
    private Integer ageGroup;
    private Boolean visibility;

    @Builder
    public CheckListDto(Long checkListId, String ownerName, Long originCheckListId, SubCategoryDto category, List<StepDto> stepItems, String title, String subTitle, String brief, Integer stepCount, Integer viewCount, Integer scrapCount, Integer progress, Integer ageGroup, Boolean visibility) {
        this.checkListId = checkListId;
        this.ownerName = ownerName;
        this.originCheckListId = originCheckListId;
        this.category = category;
        this.stepItems = stepItems;
        this.title = title;
        this.subTitle = subTitle;
        this.brief = brief;
        this.stepCount = stepCount;
        this.viewCount = viewCount;
        this.scrapCount = scrapCount;
        this.progress = progress;
        this.ageGroup = ageGroup;
        this.visibility = visibility;
    }

    static public CheckListDto createDto(CheckList checkList){
        CheckListDto dto = new CheckListDto();
        dto.checkListId = checkList.getId();
        dto.ownerName = checkList.getOwner().getName();
        dto.title = checkList.getTitle();
        dto.brief = checkList.getBrief();
        dto.visibility = checkList.getVisibility();

        dto.subTitle = checkList.getSubTitle();
        dto.stepCount = checkList.getStepCount();
        dto.viewCount = checkList.getViewCount();
        dto.scrapCount = checkList.getScrapCount();
        dto.progress = checkList.getProgress();
        dto.ageGroup = checkList.getAgeGroup();

        if(checkList.getOrigin() != null) dto.originCheckListId = checkList.getOrigin().getId();
        if(checkList.getCategory() != null) dto.category = SubCategoryDto.createDto(checkList.getCategory());

        for (StepItem stepItem : checkList.getStepItems()) {
            dto.stepItems.add(StepDto.createDto(stepItem));
        }

        return dto;
    }
}
