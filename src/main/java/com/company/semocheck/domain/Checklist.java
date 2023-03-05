package com.company.semocheck.domain;

import com.company.semocheck.domain.request.checklist.CreateChecklistRequestDto;
import com.company.semocheck.domain.request.checklist.StepRequestDto;
import com.company.semocheck.domain.request.checklist.UpdateChecklistRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
@Table(name = "checklist")
public class Checklist extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    @NotNull
    private Member owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_checklist_id")
    @JsonIgnore
    private Checklist origin;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private SubCategory category;

    @NotNull
    @Size(max = 60)
    private String title;

    @Size(max = 250)
    private String brief;

    @ColumnDefault("0")
    private Boolean publish;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private FileDetail fileDetail;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL)
    private List<Step> steps = new ArrayList<>();

    @ColumnDefault("0")
    private Integer stepCount;

    @ColumnDefault("0")
    private Integer viewCount;

    @ColumnDefault("0")
    private Integer scrapCount;

    @ColumnDefault("0")
    private Float avgAge;

    @ColumnDefault("0")
    private Integer viewCountMale;

    @ColumnDefault("0")
    private Integer viewCountFemale;

    private Integer defaultImage;

    //임시저장 페이지
    private Integer temporary;

    //====== 진행 정보 =====//
    @ColumnDefault("0")
    private Boolean complete;

    @Size(max = 10)
    @ColumnDefault("0")
    private String progress;

    //====== 생성 메서드 ======//
    static public Checklist createEntity(CreateChecklistRequestDto requestDto, Member member, SubCategory category){
        Checklist entity = new Checklist();

        entity.title = requestDto.getTitle();
        entity.brief = requestDto.getBrief();
        entity.publish = requestDto.getPublish();
        entity.temporary = requestDto.getTemporary();

        //연관관계 설정
        entity.setOwner(member); //owner
        if(category != null) entity.setCategory(category); //category
        if(requestDto.getSteps() != null){ //step
            for (StepRequestDto stepRequestDto : requestDto.getSteps()) {
                Step step = Step.createEntity(stepRequestDto, entity);
                entity.addStep(step);
            }
        }

        return entity;
    }

    static public Checklist createEntity(Checklist checklist, Member member){
        Checklist entity = new Checklist();

        entity.title = checklist.getTitle();
        entity.brief = checklist.getBrief();
        entity.publish = false;

        //연관관계 설정
        entity.setOrigin(checklist); //origin
        entity.setOwner(member); //owner
        if(checklist.getCategory() != null) entity.setCategory(checklist.getCategory()); //category
        if(checklist.getSteps() != null){ //step
            for (Step step : checklist.getSteps()) {
                Step _step = Step.createEntity(step, entity);
                entity.addStep(_step);
            }
        }

        return entity;
    }

    //====== 수정 메서드 ======//
    public void updateInfo(UpdateChecklistRequestDto requestDto, SubCategory subCategory) {
        this.title = requestDto.getTitle();
        this.brief = requestDto.getBrief();
        this.publish = requestDto.getPublish();
        this.temporary = requestDto.getTemporary();
        this.setCategory(subCategory);
    }

    public void updateProgress(){
        int total = this.steps.size();
        int checkNum = 0;
        for (Step step : this.steps) {
            if(step.getIsCheck()) checkNum += 1;
        }
        float result = checkNum / (float)total * 100f; //백분율 표시
        if(result == 100f) this.complete = true; //진행도 100%일 경우 complete true로 변경

        this.progress = String.format("%.2f", result);
    }

    public void updateInfoByViewer(Member member) {
        if(!member.getSex()){this.viewCountMale++;} // viewer = male
        else{this.viewCountFemale++;} //viewer = female


        int totalViewCount = this.viewCountFemale + this.viewCountMale;
        float totalAge = this.avgAge * (totalViewCount - 1) + member.getAge();
        this.avgAge = totalAge / totalViewCount;
    }

    public void increaseViewCount() {this.viewCount++;}

    public void increaseScrapCount() {this.scrapCount++;}
    public void decreaseScrapCount() {if(this.scrapCount > 0) this.scrapCount--;}

    //====== 연관관계 메서드======//
    public void setOwner(Member member){
        this.owner = member;
        member.addChecklist(this);
    }

    public void setCategory(SubCategory category) {this.category = category;}
    public void addStep(Step step){
        this.steps.add(step);
    }
    public void setFile(FileDetail fileDetail){this.fileDetail = fileDetail;}
    public void removeStep(Step step) { this.steps.remove(step); }
    public void setOrigin(Checklist checklist) {this.origin = checklist;}
}
