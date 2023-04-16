package com.company.semocheck.domain.checklist;

import com.company.semocheck.domain.*;
import com.company.semocheck.domain.category.SubCategory;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.request.checklist.CreateChecklistRequest;
import com.company.semocheck.domain.request.checklist.StepRequestDto;
import com.company.semocheck.domain.request.checklist.UpdateChecklistRequestDto;
import com.company.semocheck.domain.request.tempChecklist.CreateTempChecklistRequest;
import com.company.semocheck.domain.request.tempChecklist.UpdateTempChecklistRequest;
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
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
@Table(name = "checklist")
public class Checklist extends BaseTimeEntity {

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

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL)
    private List<Step> steps = new ArrayList<>();

    @ColumnDefault("0")
    private Integer stepCount;

    //====== 이미지 관련 정보 =======//

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "image_id")
    private FileDetail image;
    @OneToOne
    @JoinColumn(name = "default_image_id")
    private FileDetail defaultImage;

    private String colorCode;

    private Integer temporary; //임시저장 페이지

    //====== 카운트 정보 =======//
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "checklist", cascade = CascadeType.ALL)
    private ChecklistStats statsInfo;

    //====== 진행 정보 =====//
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "checklist", cascade = CascadeType.ALL)
    private ChecklistUsage usageInfo;

    //====== 생성 메서드 ======//
    static public Checklist createTempEntity(CreateChecklistRequest requestDto, Member member, SubCategory category){
        Checklist entity = new Checklist();
        ChecklistUsage usageInfo = new ChecklistUsage(entity);
        ChecklistStats statsInfo = new ChecklistStats(entity);

        entity.title = requestDto.getTitle();
        entity.brief = requestDto.getBrief();
        entity.publish = requestDto.getPublish();
        entity.colorCode = requestDto.getColorCode();
        entity.usageInfo = usageInfo;
        entity.statsInfo = statsInfo;

        //연관관계 설정
        entity.setOwner(member); //owner
        if(category != null) entity.setCategory(category); //category
        if(requestDto.getSteps() != null){ //step
            for (StepRequestDto stepRequestDto : requestDto.getSteps()) {
                Step step = Step.createEntity(stepRequestDto, entity);
                entity.addStep(step);
            }

            entity.stepCount = requestDto.getSteps().size();
        }

        return entity;
    }

    static public Checklist createTempEntity(CreateTempChecklistRequest requestDto, Member member, SubCategory category){
        Checklist entity = new Checklist();
        ChecklistUsage usageInfo = new ChecklistUsage(entity);
        ChecklistStats statsInfo = new ChecklistStats(entity);

        entity.title = requestDto.getTitle();
        entity.brief = requestDto.getBrief();
        entity.publish = requestDto.getPublish();
        entity.temporary = requestDto.getTemporary();
        entity.colorCode = requestDto.getColorCode();
        entity.usageInfo = usageInfo;
        entity.statsInfo = statsInfo;

        //연관관계 설정
        entity.setOwner(member); //owner
        if(category != null) entity.setCategory(category); //category
        if(requestDto.getSteps() != null){ //step
            for (StepRequestDto stepRequestDto : requestDto.getSteps()) {
                Step step = Step.createEntity(stepRequestDto, entity);
                entity.addStep(step);
            }

            entity.stepCount = requestDto.getSteps().size();
        }

        return entity;
    }

    static public Checklist copyEntity(Checklist checklist, Member member){
        Checklist entity = new Checklist();
        ChecklistUsage usageInfo = new ChecklistUsage(entity);
        ChecklistStats statsInfo = new ChecklistStats(entity);

        entity.title = checklist.getTitle();
        entity.brief = checklist.getBrief();
        entity.publish = false;
        entity.colorCode = checklist.getColorCode();
        entity.usageInfo = usageInfo;
        entity.statsInfo = statsInfo;

        //연관관계 설정
        entity.setOrigin(checklist); //origin
        entity.setOwner(member); //owner
        if(checklist.getCategory() != null) entity.setCategory(checklist.getCategory()); //category
        if(checklist.getSteps() != null){ //step
            for (Step step : checklist.getSteps()) {
                Step _step = Step.createEntity(step, entity);
                entity.addStep(_step);
            }
            entity.stepCount = checklist.getSteps().size();
        }

        return entity;
    }

    //====== 수정 메서드 ======//
    public void updateInfo(UpdateChecklistRequestDto requestDto, SubCategory subCategory) {
        this.title = requestDto.getTitle();
        this.brief = requestDto.getBrief();
        this.publish = requestDto.getPublish();
        this.colorCode = requestDto.getColorCode();
        this.setCategory(subCategory);
        this.stepCount = this.steps.size();

        if(requestDto.getTemporary() != null) this.temporary = temporary;
    }

    public void updateProgress(){
        long count = this.steps.stream().filter(step -> step.getIsCheck()).count();
        this.usageInfo.setProgress((int) (count * 100 / this.stepCount));
    }

    public void updateTempInfo(UpdateTempChecklistRequest requestDto, SubCategory subCategory) {
        this.title = requestDto.getTitle();
        this.brief = requestDto.getBrief();
        this.publish = requestDto.getPublish();
        this.temporary = requestDto.getTemporary();
        this.colorCode = requestDto.getColorCode();
        this.setCategory(subCategory);
        this.stepCount = this.steps.size();
    }

    public void updateStatsInfoByViewer(Optional<Member> member) {
        this.statsInfo.updateViewCount(member);
    }

    public void increaseScrapCount() {this.statsInfo.increaseScrapCount();}
    public void decreaseScrapCount() {if(this.statsInfo.getScrapCount() > 0) this.statsInfo.decreaseScrapCount();}

    //====== 연관관계 메서드======//
    public void setOwner(Member member){
        this.owner = member;
        member.addChecklist(this);
    }

    public void setCategory(SubCategory category) {this.category = category;}
    public void addStep(Step step){
        this.steps.add(step);
    }
    public void setImage(FileDetail image){this.image = image;}
    public void setDefaultImage(FileDetail defaultImage){this.defaultImage = defaultImage;}
    public void removeStep(Step step) { this.steps.remove(step); }
    public void setOrigin(Checklist checklist) {this.origin = checklist;}
}
