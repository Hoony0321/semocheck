package com.company.semocheck.domain.category;

import com.company.semocheck.domain.request.category.CreateCategoryRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_category_id")
    private Long id;

    @Size(max = 15)
    @NotNull
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_category_id")
    @JsonIgnore
    @NotNull
    private MainCategory mainCategory;

    static public SubCategory createEntity(CreateCategoryRequestDto requestDto, MainCategory mainCategory){
        SubCategory entity = new SubCategory();
        entity.name = requestDto.getSubName();
        entity.setMainCategory(mainCategory);
        return entity;
    }

    //====== 연관관계 메서드 =======//
    public void setMainCategory(MainCategory mainCategory) {
        this.mainCategory = mainCategory;
        mainCategory.addSubCategory(this);
    }
}
