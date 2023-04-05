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
public class ChecklistTempDetailDto {


    //Id
    private Long checklistId;

    //Info

    private String title;
    private String brief;
    private SubCategoryDto category;
    private Boolean publish;
    private List<StepPostDto> steps = new ArrayList<>();
    private Integer temporary;

    //Image
    private FileDto image;
    private FileDto defaultImage;
    private String colorCode;

    //Date
    private String createdDate;
    private String modifiedDate;


    static public ChecklistTempDetailDto createDto(Checklist checklist){
        ChecklistTempDetailDto dto = new ChecklistTempDetailDto();

        //Id
        dto.checklistId = checklist.getId();

        //Info
        dto.title = checklist.getTitle();
        dto.brief = checklist.getBrief();
        dto.publish = checklist.getPublish();
        dto.temporary = checklist.getTemporary();
        if(checklist.getCategory() != null) dto.category = SubCategoryDto.createDto(checklist.getCategory());
        for (Step step : checklist.getSteps()) {
            dto.steps.add(StepPostDto.createDto(step));
        }
        dto.steps.sort(Comparator.comparing(StepPostDto::getOrder));

        //Image
        if(checklist.getImage() != null) dto.image = FileDto.createDto(checklist.getImage());
        if(checklist.getDefaultImage() != null) dto.defaultImage = FileDto.createDto(checklist.getDefaultImage());
        dto.colorCode = checklist.getColorCode();

        //Date
        dto.createdDate = checklist.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        dto.modifiedDate = checklist.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        return dto;
    }
}
