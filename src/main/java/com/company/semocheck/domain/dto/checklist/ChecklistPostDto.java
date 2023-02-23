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
import java.util.List;

@Data
@NoArgsConstructor
public class ChecklistPostDto {

    private Long checklistId;
    private String ownerName;
    private Long originChecklistId;
    private SubCategoryDto category;
    private String title;
    private String brief;
    private List<StepPostDto> steps = new ArrayList<>();

    private Integer stepCount;
    private Integer viewCount;
    private Integer scrapCount;
    private Integer ageGroup;
    private String createdDate;
    private String modifiedDate;
    private FileDto fileDto;



    static public ChecklistPostDto createDto(Checklist checklist){
        ChecklistPostDto dto = new ChecklistPostDto();
        dto.checklistId = checklist.getId();
        dto.ownerName = checklist.getOwner().getName();
        dto.title = checklist.getTitle();
        dto.brief = checklist.getBrief();

        dto.stepCount = checklist.getStepCount();
        dto.viewCount = checklist.getViewCount();
        dto.scrapCount = checklist.getScrapCount();
        dto.ageGroup = checklist.getAgeGroup();

        dto.createdDate = checklist.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));;
        dto.modifiedDate = checklist.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));;;

        if(checklist.getOrigin() != null) dto.originChecklistId = checklist.getOrigin().getId();
        if(checklist.getCategory() != null) dto.category = SubCategoryDto.createDto(checklist.getCategory());
        if(checklist.getFileDetail() != null) dto.fileDto = FileDto.createDto(checklist.getFileDetail());

        for (Step step : checklist.getSteps()) {
            StepPostDto stepPostDto = StepPostDto.createDto(step);
            dto.steps.add(stepPostDto);
        }

        return dto;
    }
}
