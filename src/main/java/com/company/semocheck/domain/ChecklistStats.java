package com.company.semocheck.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
@Table(name = "checklist_stats")
public class ChecklistStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_stats_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id")
    @JsonIgnore
    private Checklist checklist;

    @ColumnDefault("0")
    private Integer scrapCount;
    @ColumnDefault("0")
    private Integer viewCount;
    @ColumnDefault("0")
    private Integer viewCountMale;
    @ColumnDefault("0")
    private Integer viewCountFemale;
    @ColumnDefault("0")
    private Float avgAge;

    public ChecklistStats(Checklist checklist) {
        this.checklist = checklist;
    }

    public void updateViewCount(Optional<Member> member){
        this.viewCount++;
        if(member.isEmpty()) return; // 회원이 아닐 경우 바로 리턴

        if(member.get().getSex() != null){
            if(member.get().getSex()) this.viewCountFemale++;
            else this.viewCountMale++;
        }
        updateAvgAge(member.get());
    }

    private void updateAvgAge(Member member){
        if(member.getAge() == null) return;
        int totalViewer = this.viewCountFemale + this.viewCountMale;
        float totalAge = this.avgAge * (totalViewer - 1) + member.getAge();
        this.avgAge = totalAge / totalViewer;
    }

    public void increaseScrapCount(){
        this.scrapCount++;
    }

    public void decreaseScrapCount(){
        this.scrapCount--;
    }



}
