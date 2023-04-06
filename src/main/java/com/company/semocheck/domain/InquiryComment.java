package com.company.semocheck.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class InquiryComment extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id")
    @JsonIgnore
    private Inquiry inquiry;

    @NotNull
    @Size(max = 250)
    private String content;

    @NotNull
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    static public InquiryComment createEntity(Member member, Inquiry inquiry, String content){
        InquiryComment entity = new InquiryComment();
        entity.inquiry = inquiry;
        entity.content = content;
        entity.writer = member;
        return entity;
    }

    //======= 연관관계 메서드 =======//
    public void setInquiry(Inquiry inquiry){
        this.inquiry = inquiry;
        inquiry.getInquiryComments().add(this);
    }
}
