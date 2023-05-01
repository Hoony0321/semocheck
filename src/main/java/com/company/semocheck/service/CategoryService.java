package com.company.semocheck.service;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.domain.FileDetail;
import com.company.semocheck.domain.category.SubCategory;
import com.company.semocheck.domain.category.MainCategory;
import com.company.semocheck.domain.request.category.CreateCategoryRequest;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.SubCategoryRepository;
import com.company.semocheck.repository.MainCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final MainCategoryRepository mainCategoryRepository;

    public List<SubCategory> getAllSubCategories(){
        return subCategoryRepository.findAll();
    }

    public List<MainCategory> getAllMainCategories(){
        return mainCategoryRepository.findAll();
    }

    public MainCategory findMainCategoryByName(String name){
        Optional<MainCategory> findOne = mainCategoryRepository.findByName(name);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_CATEGORY);

        return findOne.get();
    }

    public SubCategory findSubCategoryByName(String mainName, String subName){
        Optional<MainCategory> findMainOne = mainCategoryRepository.findByName(mainName);
        if(findMainOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_CATEGORY);

        MainCategory mainCategory = findMainOne.get();
        Optional<SubCategory> findSubOne = mainCategory.getSubCategoryList().stream().filter(sc -> sc.getName().equals(subName)).findAny();
        if(findSubOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_MEMBER);

        return findSubOne.get();
    }

    public List<SubCategory> findSubCategoryByOnlySubName(String subName) {
        List<SubCategory> categories = subCategoryRepository.findByName(subName);
        return categories;
    }

    @Transactional
    public void createCategory(CreateCategoryRequest requestDto) {
        String mainName = requestDto.getMainName();
        String subName = requestDto.getSubName();

        if(mainName == null) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.NOT_EXISTED_MAIN_CATEGORY);

        Optional<MainCategory> findOne = mainCategoryRepository.findByName(mainName);

        if(subName == null){ //1차 카테고리 생성
            if(findOne.isPresent()) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.EXISTED_CATEGORY);
            MainCategory mainCategory = MainCategory.createEntity(requestDto);
            mainCategoryRepository.save(mainCategory);
        }

        if(subName != null){ //2차 카테고리 생성
            if(findOne.isEmpty()) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.NOT_EXISTED_MAIN_CATEGORY);

            //메인 카테고리에 똑같은 이름의 서브 카테고리 있는지 확인
            MainCategory mainCategory = findOne.get();
            mainCategory.getSubCategoryList().stream().forEach(subCategory -> {
                if(subCategory.getName().equals(requestDto.getSubName())) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.EXISTED_CATEGORY);
            });

            SubCategory subCategory = SubCategory.createEntity(requestDto, findOne.get());
            subCategoryRepository.save(subCategory);
        }
    }

    @Transactional
    public void removeSubCategory(String mainName, String subName) {
        Optional<MainCategory> findMainOne;
        Optional<SubCategory> findSubOne;

        findMainOne = mainCategoryRepository.findByName(mainName);
        if(findMainOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_EXISTED_MAIN_CATEGORY);

        MainCategory mainCategory = findMainOne.get();
        findSubOne = mainCategory.getSubCategoryList().stream().filter(sc -> sc.getName().equals(subName)).findFirst();
        if(findSubOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_EXISTED_SUB_CATEGORY);
        SubCategory subCategory = findSubOne.get();

        //연관관계 삭제
        mainCategory.removeSubCategory(subCategory);

        //Entity 삭제
        subCategoryRepository.delete(subCategory);
    }

    @Transactional
    public void removeMainCategory(String name) {
        Optional<MainCategory> findOne = mainCategoryRepository.findByName(name);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_CATEGORY);
        MainCategory mainCategory = findOne.get();

        if(mainCategory.getSubCategoryList().size() > 0){ throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.EXISTED_SUB_CATEGORY); }
        mainCategoryRepository.delete(mainCategory);
    }

    @Transactional
    public void setMainCategoryFile(String name, FileDetail fileDetail){
        Optional<MainCategory> findOne = mainCategoryRepository.findByName(name);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_CATEGORY);
        MainCategory mainCategory = findOne.get();
    }

    @Transactional
    public void setSubCategoryFile(String mainName, String subName, FileDetail fileDetail){
        Optional<MainCategory> findMainOne;
        Optional<SubCategory> findSubOne;

        findMainOne = mainCategoryRepository.findByName(mainName);
        if(findMainOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_EXISTED_MAIN_CATEGORY);

        MainCategory mainCategory = findMainOne.get();
        findSubOne = mainCategory.getSubCategoryList().stream().filter(sc -> sc.getName().equals(subName)).findFirst();
        if(findSubOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_EXISTED_SUB_CATEGORY);
        SubCategory subCategory = findSubOne.get();
    }


    public SubCategory findSubCategoryById(Long id) {
        Optional<SubCategory> findOne = subCategoryRepository.findById(id);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_CATEGORY);

        return findOne.get();
    }
}
