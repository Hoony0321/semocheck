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
public class ChecklistTempSimpleDto {


    //Id
    private Long checklistId;

    //Info

    private String title;
    private SubCategoryDto category;
    private Integer temporary;

    //Image
    private Integer defaultImage;
    private FileDto fileDto;

    //Date
    private String createdDate;
    private String modifiedDate;


    static public ChecklistTempSimpleDto createDto(Checklist checklist){
        ChecklistTempSimpleDto dto = new ChecklistTempSimpleDto();

        //Id
        dto.checklistId = checklist.getId();

        //Info
        dto.title = checklist.getTitle();
        dto.temporary = checklist.getTemporary();
        if(checklist.getCategory() != null) dto.category = SubCategoryDto.createDto(checklist.getCategory());

        //Image
        dto.defaultImage = checklist.getDefaultImage();
        if(checklist.getFileDetail() != null) dto.fileDto = FileDto.createDto(checklist.getFileDetail());

        //Date
        dto.createdDate = checklist.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        dto.modifiedDate = checklist.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        return dto;
    }
}
