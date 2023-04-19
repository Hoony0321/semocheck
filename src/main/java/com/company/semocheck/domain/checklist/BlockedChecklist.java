package com.company.semocheck.domain.checklist;

import com.company.semocheck.domain.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "blocked_checklist")
public class BlockedChecklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blocked_checklist_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id")
    @JsonIgnore
    @NotNull
    private Checklist checklist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    @NotNull
    private Member member;

    static public BlockedChecklist createEntity(Member member, Checklist checklist){
        BlockedChecklist entity = new BlockedChecklist();
        entity.setMember(member);
        entity.setChecklist(checklist);
        return entity;
    }


    //====== 연관관계 메서드 ======//
    private void setMember(Member member) {this.member = member;}
    private void setChecklist(Checklist checklist){this.checklist = checklist;}
}
