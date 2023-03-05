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
public class ChecklistTempDto extends ChecklistSimpleDto {

    //post info
    private String ownerName;
    private Long originChecklistId;
    private String brief;
    private List<StepPostDto> steps = new ArrayList<>();

    //detail info
    private Boolean publish;

    //temp info
    private Integer temporary;

    static public ChecklistTempDto createDto(Checklist checklist){
        ChecklistTempDto dto = new ChecklistTempDto();
        dto.checklistId = checklist.getId();
        dto.ownerName = checklist.getOwner().getName();
        dto.title = checklist.getTitle();
        dto.brief = checklist.getBrief();
        dto.publish = checklist.getPublish();
        dto.temporary = checklist.getTemporary();

        dto.stepCount = checklist.getStepCount();
        dto.viewCount = checklist.getViewCount();
        dto.scrapCount = checklist.getScrapCount();


        dto.createdDate = checklist.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));;
        dto.modifiedDate = checklist.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));;;

        if(checklist.getOrigin() != null) dto.originChecklistId = checklist.getOrigin().getId();
        if(checklist.getCategory() != null) dto.category = SubCategoryDto.createDto(checklist.getCategory());
        if(checklist.getFileDetail() != null) dto.fileDto = FileDto.createDto(checklist.getFileDetail());

        for (Step step : checklist.getSteps()) {
            StepPostDto stepPostDto = StepPostDto.createDto(step);
            dto.steps.add(stepPostDto);
        }

        dto.steps.sort(Comparator.comparing(StepPostDto::getOrder));
        return dto;
    }
}
