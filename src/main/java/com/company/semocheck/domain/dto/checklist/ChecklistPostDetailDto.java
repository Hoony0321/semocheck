package com.company.semocheck.domain.dto.checklist;

import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.Scrap;
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
import java.util.Optional;

@Data
@NoArgsConstructor
public class ChecklistPostDetailDto {

    //Id
    private Long checklistId;

    //Info
    private String title;
    private String brief;
    private String ownerName;
    private SubCategoryDto category;
    private List<StepPostDto> steps = new ArrayList<>();
    private Integer stepCount;
    private Integer viewCount;
    private Integer scrapCount;
    private Float avgAge;
    private Boolean avgSex;
    private Boolean isScrap;
    private Boolean isOwner;

    //Image
    private FileDto image;
    private FileDto defaultImage;
    private String colorCode;


    //Date
    private String createdDate;
    private String modifiedDate;

    static public ChecklistPostDetailDto createDto(Checklist checklist, Optional<Member> findOne){
        ChecklistPostDetailDto dto = new ChecklistPostDetailDto();

        //Id
        dto.checklistId = checklist.getId();

        //Info
        dto.ownerName = checklist.getOwner().getName();
        dto.title = checklist.getTitle();
        dto.brief = checklist.getBrief();
        dto.stepCount = checklist.getStepCount();
        dto.viewCount = checklist.getStatsInfo().getViewCount();
        dto.scrapCount = checklist.getStatsInfo().getScrapCount();
        if(checklist.getCategory() != null) dto.category = SubCategoryDto.createDto(checklist.getCategory());
        if(checklist.getStatsInfo().getViewCount() >= 10) { // 조회수 10회 이상일 경우만
            dto.avgAge = checklist.getStatsInfo().getAvgAge();
            dto.avgSex = checklist.getStatsInfo().getViewCountFemale() > checklist.getStatsInfo().getViewCountMale();
        }

        if(findOne.isEmpty()){
            dto.isScrap = false;
            dto.isOwner = false;
        }
        else{
            Member member = findOne.get();
            Optional<Scrap> findScrap = member.getScraps().stream().filter(s -> s.getChecklist().getId().equals(checklist.getId())).findFirst();
            if(findScrap.isEmpty()) dto.isScrap = false;
            else dto.isScrap = true;

            if(checklist.getOwner().equals(member)) dto.isOwner = true;
            else dto.isOwner = false;
        }

        for(Step step : checklist.getSteps()) {
            StepPostDto stepPostDto = StepPostDto.createDto(step);
            dto.steps.add(stepPostDto);
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
