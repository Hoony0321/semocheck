package com.company.semocheck.domain.checklist;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.domain.*;
import com.company.semocheck.domain.category.SubCategory;
import com.company.semocheck.form.CreateChecklistForm;
import com.company.semocheck.form.CreateStepForm;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.request.checklist.UpdateChecklistRequestDto;
import com.company.semocheck.domain.request.tempChecklist.CreateTempChecklistRequest;
import com.company.semocheck.domain.request.tempChecklist.UpdateTempChecklistRequest;
import com.company.semocheck.exception.GeneralException;
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

    //===== 체크리스트 종류 ======//
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private ChecklistType type;

    @NotNull
    private Boolean isCopied = false;
    private Integer temporary; //임시저장 페이지

    //====== 카운트 정보 =======//
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "checklist", cascade = CascadeType.ALL)
    private ChecklistStats statsInfo;

    //====== 진행 정보 =====//
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "checklist", cascade = CascadeType.ALL)
    private ChecklistUsage usageInfo;

    //====== 생성 메서드 ======//
    static public Checklist createEntity(CreateChecklistForm form, Member member, SubCategory category){
        Checklist entity = new Checklist();
        ChecklistUsage usageInfo = new ChecklistUsage(entity);
        ChecklistStats statsInfo = new ChecklistStats(entity);

        entity.title = form.getTitle();
        entity.brief = form.getBrief();
        entity.publish = form.getPublish();
        entity.colorCode = form.getColorCode();
        entity.isCopied = Boolean.FALSE;
        entity.type = ChecklistType.NORMAL;
        entity.usageInfo = usageInfo;
        entity.statsInfo = statsInfo;

        //연관관계 설정
        entity.setOwner(member); //owner
        if(category != null) entity.setCategory(category); //category
        if(form.getSteps() != null){ //step

            for(int order=1; order<=form.getSteps().size(); order++){
                CreateStepForm createStepForm = form.getSteps().get(order-1);
                Step step = Step.createEntity(createStepForm, entity, order);
                entity.addStep(step);
            }
            entity.stepCount = form.getSteps().size();
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
        entity.isCopied = Boolean.FALSE;
        entity.type = ChecklistType.TEMPORARY;
        entity.usageInfo = usageInfo;
        entity.statsInfo = statsInfo;

        //연관관계 설정
        entity.setOwner(member); //owner
        if(category != null) entity.setCategory(category); //category
        if(requestDto.getSteps() != null){ //step
            for(int order=1; order<=requestDto.getSteps().size(); order++){
                CreateStepForm createStepForm = requestDto.getSteps().get(order-1);
                Step step = Step.createEntity(createStepForm, entity, order);
                entity.addStep(step);
            }

            entity.stepCount = requestDto.getSteps().size();
        }

        return entity;
    }

    static public Checklist createCopyEntity(Checklist checklist, Member member){
        Checklist entity = new Checklist();
        ChecklistUsage usageInfo = new ChecklistUsage(entity);
        ChecklistStats statsInfo = new ChecklistStats(entity);

        entity.title = checklist.getTitle();
        entity.brief = checklist.getBrief();
        entity.type = ChecklistType.NORMAL;
        entity.publish = false;
        entity.isCopied = true;
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

    public void updatePublish(Boolean publish){
        if(this.isCopied && publish) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.NOT_PUBLISHED);
        this.publish = publish;
    }
    public void updateInfo(UpdateChecklistRequestDto requestDto, SubCategory subCategory) {
        this.title = requestDto.getTitle();
        this.brief = requestDto.getBrief();
        this.colorCode = requestDto.getColorCode();
        this.stepCount = this.steps.size();
        this.updatePublish(requestDto.getPublish());
        this.setCategory(subCategory);
    }

    public void updateTempInfo(UpdateTempChecklistRequest requestDto, SubCategory subCategory) {
        this.title = requestDto.getTitle();
        this.brief = requestDto.getBrief();
        this.colorCode = requestDto.getColorCode();
        this.stepCount = this.steps.size();
        this.updatePublish(requestDto.getPublish());
        this.setCategory(subCategory);

        this.temporary = requestDto.getTemporary();
        if(this.temporary == null){ this.type = ChecklistType.NORMAL; }
    }

    public void updateProgress(){
        long count = this.steps.stream().filter(step -> step.getIsCheck()).count();
        this.usageInfo.setProgress((int) (count * 100 / this.stepCount));
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
