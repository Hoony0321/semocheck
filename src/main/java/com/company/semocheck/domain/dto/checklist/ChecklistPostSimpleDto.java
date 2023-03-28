package com.company.semocheck.domain.dto.checklist;

import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.Scrap;
import com.company.semocheck.domain.dto.FileDto;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public class ChecklistPostSimpleDto {

    //Id
    private Long checklistId;

    //Info
    private String title;
    private SubCategoryDto category;
    private Integer stepCount;
    private Float avgAge;
    private Boolean avgSex;
    private Boolean isScrap;

    //Image
    private Integer defaultImage;
    private FileDto fileDto;

    //Date
    private String createdDate;
    private String modifiedDate;

    static public ChecklistPostSimpleDto createDto(Checklist checklist, Optional<Member> findOne) {
        ChecklistPostSimpleDto dto = new ChecklistPostSimpleDto();
        //Id
        dto.checklistId = checklist.getId();

        //Info
        dto.title = checklist.getTitle();
        dto.stepCount = checklist.getStepCount();
        dto.isScrap = null;
        if(checklist.getCategory() != null) dto.category = SubCategoryDto.createDto(checklist.getCategory());
        if(checklist.getViewCount() >= 10) { // 조회수 10회 이상일 경우만
            dto.avgAge = checklist.getAvgAge();
            dto.avgSex = checklist.getViewCountFemale() > checklist.getViewCountMale();
        }
        if(findOne.isEmpty()) dto.isScrap = null;
        else {
            Optional<Scrap> scrap = findOne.get().getScraps().stream().filter(s -> s.getChecklist().getId().equals(checklist.getId())).findFirst();
            if(scrap.isPresent()) dto.isScrap = true;
            else dto.isScrap = false;
        }

        //Image
        dto.defaultImage = checklist.getDefaultImage();
        if(checklist.getFileDetail() != null) dto.fileDto = FileDto.createDto(checklist.getFileDetail());

        //Date
        dto.createdDate = checklist.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        dto.modifiedDate = checklist.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        return dto;
    }
}
