package com.company.semocheck.domain;

import com.company.semocheck.domain.dto.request.checkList.CreateCheckListRequestDto;
import com.company.semocheck.domain.dto.request.checkList.StepRequestDto;
import com.company.semocheck.domain.dto.request.checkList.UpdateCheckListRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
@Table(name = "checklist")
public class CheckList extends BaseTimeEntity{

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
    private CheckList origin;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private SubCategory category;

    @NotNull
    @Size(max = 60)
    private String title;

    @Size(max = 255)
    private String brief;

    @ColumnDefault("0")
    private Boolean visibility;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private FileDetail fileDetail;

    @OneToMany(mappedBy = "checkList", cascade = CascadeType.ALL)
    private List<Step> steps = new ArrayList<>();

    @ColumnDefault("0")
    private Integer stepCount;

    @ColumnDefault("0")
    private Integer viewCount;

    @ColumnDefault("0")
    private Integer scrapCount;

    private Integer ageGroup;

    //====== 진행 정보 =====//
    @ColumnDefault("0")
    private Boolean complete;

    @Size(max = 10)
    @ColumnDefault("0")
    private String progress;


    static public CheckList createEntity(CreateCheckListRequestDto requestDto, Member member, SubCategory category){
        CheckList entity = new CheckList();

        entity.title = requestDto.getTitle();
        entity.brief = requestDto.getBrief();
        entity.visibility = requestDto.getVisibility();

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

    //====== 수정 메서드 ======//
    public void updateInfo(UpdateCheckListRequestDto requestDto, SubCategory subCategory) {
        if(requestDto.getTitle() != null) this.title = requestDto.getTitle();
        if(requestDto.getBrief() != null) this.brief = requestDto.getBrief();
        if(requestDto.getVisibility() != null) this.visibility = requestDto.getVisibility();
        if(subCategory != null) this.setCategory(subCategory);
    }

    //====== 연관관계 메서드======//
    public void setOwner(Member member){
        this.owner = member;
        member.addCheckList(this);
    }

    public void setCategory(SubCategory category) {this.category = category;}
    public void addStep(Step step){
        this.steps.add(step);
    }
    public void setFile(FileDetail fileDetail){this.fileDetail = fileDetail;}
    public void removeStep(Step step) { this.steps.remove(step); }
}
