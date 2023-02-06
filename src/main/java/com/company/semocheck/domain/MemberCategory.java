package com.company.semocheck.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class MemberCategory {

    @Id
    @Column(name = "member_category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @JsonIgnore
    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @NotNull
    @JsonIgnore
    @JoinColumn(name = "category_id")
    @ManyToOne
    private SubCategory subCategory;

    static public MemberCategory createEntity(Member member, SubCategory subCategory){
        MemberCategory memberCategory = new MemberCategory();
        memberCategory.setMember(member);
        memberCategory.setSubCategory(subCategory);
        return memberCategory;
    }

    //====== 연관관계 메서드 ======//

    public void setMember(Member member) {this.member = member;}

    public void setSubCategory(SubCategory subCategory) {this.subCategory = subCategory;}
}
