package com.company.semocheck.service;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.domain.*;
import com.company.semocheck.domain.dto.request.checkList.CreateCheckListRequestDto;
import com.company.semocheck.domain.dto.request.checkList.CreateStepRequestDto;
import com.company.semocheck.domain.dto.request.checkList.StepRequestDto;
import com.company.semocheck.domain.dto.request.checkList.UpdateCheckListRequestDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.CheckListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CheckListService {

    private final CheckListRepository checkListRepository;
    private final CategoryService categoryService;
    private final FileService fileService;

    @Transactional
    public CheckList findById(Long id){
        Optional<CheckList> findOne = checkListRepository.findById(id);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, "해당 정보의 체크리스트는 존재하지 않습니다.");

        return findOne.get();
    }

    @Transactional
    public Long createCheckList(CreateCheckListRequestDto requestDto, Member member, MultipartFile imgFile){
        SubCategory category = null;

        //Category Entity 찾기
        if(requestDto.getMainCategoryName() != null && requestDto.getSubCategoryName() != null){
            category = categoryService.findSubCategoryByName(requestDto.getMainCategoryName(), requestDto.getSubCategoryName());
        }
        else if(requestDto.getSubCategoryName() != null) {
            throw new GeneralException(Code.BAD_REQUEST, "1차 카테고리가 정의되지 않았습니다.");
        }

        //CheckList 생성
        CheckList checkList = CheckList.createEntity(requestDto, member, category);

        //Image File 설정
        if(imgFile != null){
            FileDetail file = fileService.upload("checklist/image", imgFile);
            checkList.setFile(file);
        }


        //CheckList 저장
        checkListRepository.save(checkList);

        return checkList.getId();
    }

    @Transactional
    public void removeCheckList(CheckList checkList) {
        checkListRepository.delete(checkList);
    }

    @Transactional
    public void updateCheckList(UpdateCheckListRequestDto requestDto, CheckList checkList) {
        SubCategory subCategory = null;
        if(requestDto.getMainCategoryName() != null && requestDto.getSubCategoryName() != null) {
            subCategory = categoryService.findSubCategoryByName(requestDto.getMainCategoryName(), requestDto.getSubCategoryName());
        }
        checkList.updateInfo(requestDto,subCategory);
    }

    @Transactional
    public void addStepItem(CreateStepRequestDto requestDto, CheckList checkList) {
        for (StepRequestDto dto : requestDto.getSteps()) {
            StepItem stepItem = StepItem.createEntity(dto, checkList);
            checkList.addStep(stepItem);
        }
    }

    public List<CheckList> findAllVisible() {
        List<CheckList> checkLists = new ArrayList<>();
        for (CheckList checkList : checkListRepository.findAll()) {
            if(checkList.getVisibility()) checkLists.add(checkList);
        }

        return checkLists;
    }
}
