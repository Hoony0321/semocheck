package com.company.semocheck.domain;

import com.company.semocheck.domain.request.inquiry.CreateInquiryRequest;
import com.company.semocheck.domain.request.inquiry.UpdateInquiryRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
public class Inquiry extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long id;

    @NotNull
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Size(max = 50)
    @NotNull
    private String title;

    @Size(max = 500)
    @NotNull
    private String content;

    @NotNull
    private InquiryStatus status;

    @NotNull
    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL)
    private List<InquiryComment> inquiryComments = new ArrayList<>();

    static public Inquiry createEntity(Member member, CreateInquiryRequest requestDto){
        Inquiry entity = new Inquiry();
        entity.title = requestDto.getTitle();
        entity.content = requestDto.getContent();
        entity.status = InquiryStatus.STAND_BY;
        return entity;
    }

    //====== 연관관계 메서드 ======//
    public void addInquiryComment(InquiryComment inquiryComment){
        this.status = InquiryStatus.COMPLETED;
        this.inquiryComments.add(inquiryComment);
    }

    public void setMember(Member member){
        this.member = member;
    }

    public void updateInfo(UpdateInquiryRequest updateRequest) {
        if(updateRequest.getTitle() != null) this.title = updateRequest.getTitle();
        if(updateRequest.getContent() != null) this.content = updateRequest.getContent();
    }
}
