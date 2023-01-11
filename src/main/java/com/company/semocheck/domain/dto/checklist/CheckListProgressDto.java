package com.company.semocheck.domain.dto.checklist;

import com.company.semocheck.domain.CheckList;
import com.company.semocheck.domain.CheckListProgress;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class CheckListProgressDto {

    //TODO JSON으로 변경하기
    private String checkInfo;
    private Integer progressRate;
    private Boolean complete;
    private String modifiedDate;

    public static CheckListProgressDto createDto(CheckListProgress checkListProgress){
        JSONParser jsonParser = new JSONParser();
        CheckListProgressDto dto = new CheckListProgressDto();
        dto.checkInfo = checkListProgress.getCheckInfo();
        dto.complete = checkListProgress.getComplete();
        dto.modifiedDate = checkListProgress.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        return dto;
    }
}
