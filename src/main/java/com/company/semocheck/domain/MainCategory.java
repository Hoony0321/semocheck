package com.company.semocheck.domain;

import com.company.semocheck.domain.dto.request.category.CreateMainCategoryRequestDto;
import com.company.semocheck.domain.dto.request.category.UpdateMainCategoryRequestDto;
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

    @OneToMany(mappedBy = "mainCategory")
    private List<SubCategory> subCategoryList = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "file_id")
    private FileDetail fileDetail;

    static public MainCategory createEntity(CreateMainCategoryRequestDto requestDto){
        MainCategory entity = new MainCategory();
        entity.name = requestDto.getName();
        return entity;
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
    public void setFile(FileDetail file){ this.fileDetail = file; }


}
