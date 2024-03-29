package com.company.semocheck.domain.category;

import com.company.semocheck.domain.request.category.CreateCategoryRequest;
import com.company.semocheck.domain.request.category.UpdateMainCategoryRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class MainCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "main_category_id")
    private Long id;

    @Size(max = 15)
    @NotNull
    private String name;

    private Integer count = 0;
    @OneToMany(mappedBy = "mainCategory")
    private List<SubCategory> subCategoryList = new ArrayList<>();

    static public MainCategory createEntity(CreateCategoryRequest requestDto){
        MainCategory entity = new MainCategory();
        entity.name = requestDto.getMainName();
        return entity;
    }

    public static MainCategory createEntity(String mainCategoryName) {
        MainCategory mainCategory = new MainCategory();
        mainCategory.name = mainCategoryName;
        return mainCategory;
    }

    //====== 수정 메서드 ======//
    public void update(UpdateMainCategoryRequestDto requestDto) {
        this.name = requestDto.getName();
    }

    //====== 연관관계 메서드 =======//
    public void addSubCategory(SubCategory subCategory){
        this.subCategoryList.add(subCategory);
    }
    public void removeSubCategory(SubCategory subCategory){ this.subCategoryList.remove(subCategory); }


    public void increaseCount() {
        this.count++;
    }
}
