package com.company.semocheck.domain.dto.checklist;

import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.dto.FileDto;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

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
    private FileDto image;
    private FileDto defaultImage;
    private String colorCode;

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
        if(checklist.getImage() != null) dto.image = FileDto.createDto(checklist.getImage());
        if(checklist.getDefaultImage() != null) dto.defaultImage = FileDto.createDto(checklist.getDefaultImage());
        dto.colorCode = checklist.getColorCode();

        //Date
        dto.createdDate = checklist.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        dto.modifiedDate = checklist.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        return dto;
    }
}
