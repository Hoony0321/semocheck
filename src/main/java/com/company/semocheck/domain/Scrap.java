package com.company.semocheck.domain;

import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Scrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id")
    @JsonIgnore
    @NotNull
    private Checklist checklist;

    static public Scrap createEntity(Member member, Checklist checklist){
        Scrap entity = new Scrap();
        entity.setMember(member);
        entity.setChecklist(checklist);
        return entity;
    }

    //====== 연관관계 메서드 ======//
    private void setMember(Member member) {
        this.member = member;
    }

    private void setChecklist(Checklist checklist){
        this.checklist = checklist;
    }
}
