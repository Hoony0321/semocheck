package com.company.semocheck.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@DynamicInsert
@Table(name = "checklist_usage")
public class ChecklistUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_usage_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id")
    @JsonIgnore
    private Checklist checklist;

    @ColumnDefault("0")
    private Boolean complete;

    @ColumnDefault("0")
    private Integer progress;

    @Column(name = "checked_date")
    private LocalDateTime checkedDate;

    public ChecklistUsage(Checklist checklist) {
        this.checklist = checklist;
    }


    public void setProgress(Integer progress) {
        this.progress = progress;
        this.complete = this.progress == 100;
        updateCheckedDate(); //체크일자 업데이트
    }

    private void updateCheckedDate() {this.checkedDate = LocalDateTime.now();}
}
