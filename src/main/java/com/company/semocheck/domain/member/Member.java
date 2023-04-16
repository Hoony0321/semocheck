package com.company.semocheck.domain.member;

import com.company.semocheck.auth.oauth2.OAuth2Attributes;
import com.company.semocheck.domain.BaseTimeEntity;
import com.company.semocheck.domain.Report;
import com.company.semocheck.domain.Scrap;
import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.dto.Role;
import com.company.semocheck.domain.inquiry.Inquiry;
import com.company.semocheck.domain.request.member.CreateMemberRequest;
import com.company.semocheck.domain.request.member.UpdateMemberRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @NotNull
    @Column(name = "oauth_id", nullable = false)
    private String oAuthId;

    @Size(max = 20)
    @NotNull
    @Column(nullable = false)
    private String provider;

    @Size(max = 50)
    @NotNull
    @Column(nullable = false)
    private String email;

    @NotNull
    @Size(max = 15)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private String picture;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @NotNull
    @Column(nullable = false)
    private Boolean agreeNotify;

    private Boolean sex;

    private Integer age;

    @OneToMany(mappedBy = "owner")
    private List<Checklist> checklists = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Scrap> scraps = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberCategory> categories = new ArrayList<>();

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL)
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Inquiry> inquiries = new ArrayList<>();

    @Builder
    public Member(String oAuthId, String provider, String email, String name, String picture, Role role, Boolean agreeNotify, Boolean sex, Integer age) {
        this.oAuthId = oAuthId;
        this.provider = provider;
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.role = role;
        this.agreeNotify = agreeNotify;
        this.sex = sex;
        this.age = age;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }

    static public Member createEntity(OAuth2Attributes attributes, String provider){
        Member entity = new Member();
        entity.oAuthId = attributes.getId();
        entity.provider = provider;
        entity.email = attributes.getEmail();
        entity.picture = attributes.getPicture();
        entity.name = attributes.getName();
        entity.role = Role.USER;
        entity.agreeNotify = false;

        return entity;
    }

    //====== 기타 메서드 ======//
    public List<Checklist> getChecklists() {
        return checklists.stream().filter(chk -> chk.getTemporary() == null).toList();
    }

    public List<Checklist> getTempChecklists() {
        return checklists.stream().filter(chk -> chk.getTemporary() != null).toList();
    }

    //====== 연관관계 메서드 ======//
    public void addChecklist(Checklist checklist){
        this.checklists.add(checklist);
    }
    public void removeChecklist(Checklist checklist) {this.checklists.remove(checklist);}
    public void addScrap(Scrap scrap) { this.scraps.add(scrap); }
    public void removeScrap(Scrap scrap) {this.scraps.remove(scrap);}
    public void addCategory(MemberCategory category){ this.categories.add(category); }
    public void removeCategory(MemberCategory category){this.categories.remove(category);}
    public void setCategory(List<MemberCategory> category){
        this.categories.clear();
        this.categories.addAll(category);
    }
    public void addReport(Report report){ this.reports.add(report); }
    public void removeReport(Report report){ this.reports.remove(report); }
    public void addInquiry(Inquiry inquiry){this.inquiries.add(inquiry);}
    public void removeInquiry(Inquiry inquiry) {this.inquiries.remove(inquiry);}

    //====== 정보 수정 메서드 ======//
    public void setInfoNewMember(CreateMemberRequest createMemberRequest){
        this.age = createMemberRequest.getAge();
        this.sex = createMemberRequest.getSex();
        this.agreeNotify = createMemberRequest.getAgreeNotify();
    }

    public void updateInfo(UpdateMemberRequest requestDto) {
        if(requestDto.getAgreeNotify() != null) this.agreeNotify = requestDto.getAgreeNotify();
        if(requestDto.getName() != null) this.name = requestDto.getName();
        if(requestDto.getAge() != null) this.age = requestDto.getAge();
    }

}
