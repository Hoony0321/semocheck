package com.company.semocheck.domain.dto.checklist;

import com.company.semocheck.domain.CheckList;
import com.company.semocheck.domain.dto.FileDto;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class CheckListPostDto {

    private Long checkListId;
    private String ownerName;
    private Long originCheckListId;
    private SubCategoryDto category;
    private String title;
    private String brief;

    private Integer stepCount;
    private Integer viewCount;
    private Integer scrapCount;
    private Integer ageGroup;
    private Boolean visibility;
    private String createdDate;
    private String modifiedDate;

    private FileDto fileDto;

    static public CheckListPostDto createDto(CheckList checkList){
        CheckListPostDto dto = new CheckListPostDto();
        dto.checkListId = checkList.getId();
        dto.ownerName = checkList.getOwner().getName();
        dto.title = checkList.getTitle();
        dto.brief = checkList.getBrief();
        dto.visibility = checkList.getVisibility();

        dto.stepCount = checkList.getStepCount();
        dto.viewCount = checkList.getViewCount();
        dto.scrapCount = checkList.getScrapCount();
        dto.ageGroup = checkList.getAgeGroup();

        dto.createdDate = checkList.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));;
        dto.modifiedDate = checkList.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));;;

        if(checkList.getOrigin() != null) dto.originCheckListId = checkList.getOrigin().getId();
        if(checkList.getCategory() != null) dto.category = SubCategoryDto.createDto(checkList.getCategory());
        if(checkList.getFileDetail() != null) dto.fileDto = FileDto.createDto(checkList.getFileDetail());

        return dto;
    }
}
