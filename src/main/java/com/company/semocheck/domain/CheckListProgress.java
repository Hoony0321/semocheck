package com.company.semocheck.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
@Table(name = "checklist_progress")
public class CheckListProgress extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_progress_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id")
    @JsonIgnore
    private CheckList checkList;

    @NotNull
    @Column(name = "check_info")
    private String checkInfo;

    @ColumnDefault("0")
    private Boolean complete;

    public static CheckListProgress createEntity(CheckList checkList) {
        CheckListProgress entity = new CheckListProgress();
        entity.checkList = checkList;

        //TODO : 추후에 step 개수를 받아서 생성하기
        entity.checkInfo = "0 0 0 0 0";
        entity.complete = false;
        return entity;
    }
}
