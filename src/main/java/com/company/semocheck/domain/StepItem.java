package com.company.semocheck.domain;

import com.company.semocheck.domain.dto.request.checkList.StepRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class StepItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long id;

    @Size(max = 30)
    @NotNull
    private String name;

    @NotNull
    private Integer stepOrder;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id")
    @JsonIgnore
    private CheckList checkList;

    static public StepItem createEntity(StepRequestDto requestDto, CheckList checkList){
        StepItem entity = new StepItem();
        entity.name = requestDto.getName();
        entity.stepOrder = requestDto.getOrder();
        entity.description = requestDto.getDescription();
        entity.setCheckList(checkList);
        return entity;
    }

    //====== 연관관계 메서드 ======//
    public void setCheckList(CheckList checkList) {
        this.checkList = checkList;
    }
}
