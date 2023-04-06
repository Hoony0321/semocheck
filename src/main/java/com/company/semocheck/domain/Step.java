package com.company.semocheck.domain;

import com.company.semocheck.domain.request.checklist.StepRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long id;
    @Size(max = 30)
    @NotNull
    private String name;
    @NotNull
    private Integer stepOrder;
    @Size(max = 100)
    private String description;

    @NotNull
    @ColumnDefault("0")
    private Boolean isCheck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id")
    @JsonIgnore
    @NotNull
    private Checklist checklist;

    static public Step createEntity(StepRequestDto requestDto, Checklist checklist){
        Step entity = new Step();
        entity.name = requestDto.getName();
        entity.stepOrder = requestDto.getOrder();
        entity.description = requestDto.getDescription();
        entity.isCheck = false;
        entity.setChecklist(checklist);
        return entity;
    }

    static public Step createEntity(Step step, Checklist checklist){
        Step entity = new Step();
        entity.name = step.getName();
        entity.stepOrder = step.getStepOrder();
        entity.description = step.getDescription();
        entity.isCheck = false;
        entity.setChecklist(checklist);
        return entity;
    }

    //====== 수정 메서드 ======//
    public void update(StepRequestDto dto){
        this.name = dto.getName();
        this.stepOrder = dto.getOrder();
        this.description = dto.getDescription();
    }

    public void update(boolean isCheck){
        this.isCheck = isCheck;
    }

    //====== 연관관계 메서드 ======//
    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }
}
