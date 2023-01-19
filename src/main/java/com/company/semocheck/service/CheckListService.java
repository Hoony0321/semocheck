package com.company.semocheck.service;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.domain.*;
import com.company.semocheck.domain.dto.request.checkList.CreateCheckListRequestDto;
import com.company.semocheck.domain.dto.request.checkList.CreateStepRequestDto;
import com.company.semocheck.domain.dto.request.checkList.StepRequestDto;
import com.company.semocheck.domain.dto.request.checkList.UpdateCheckListRequestDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.CheckListRepository;
import com.company.semocheck.repository.StepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CheckListService {

    private final CheckListRepository checkListRepository;
    private final CategoryService categoryService;
    private final FileService fileService;
    private final StepRepository stepRepository;
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
    public void updateCheckList(CheckList checkList, UpdateCheckListRequestDto requestDto, MultipartFile imgFile) {
        //category validation
        SubCategory subCategory = null;
        if(requestDto.getMainCategoryName() != null && requestDto.getSubCategoryName() != null) {
            subCategory = categoryService.findSubCategoryByName(requestDto.getMainCategoryName(), requestDto.getSubCategoryName());
        }

        if(requestDto.getSteps() != null){
            //step validation
            List<Integer> orderList = new ArrayList<>();
            requestDto.getSteps().stream().forEach(s -> orderList.add(s.getOrder()));
            Collections.sort(orderList);
            for(int i = 1; i <= orderList.size(); i++){
                if(!orderList.get(i-1).equals(i)) throw new GeneralException(Code.BAD_REQUEST, "스텝의 순서 오류");
            }

            //step update & add & delete
            for (Step step : checkList.getSteps()) {
                Optional<StepRequestDto> findOne = requestDto.getSteps().stream().filter(s -> s.getStepId().equals(step.getId())).findFirst();
                if(findOne.isEmpty()){ //정보가 없는 스텝 삭제
                    checkList.removeStep(step);
                    stepRepository.delete(step);
                }
                else{ step.update(findOne.get()); } //기존 스텝 정보 수정
            }

            for (StepRequestDto step : requestDto.getSteps()) { //새로운 스텝 추가
                if(step.getStepId().equals(-1L)){
                    Step stepEntity = Step.createEntity(step, checkList);
                    checkList.addStep(stepEntity);
                }
            }
        }

        //image file update
        if(imgFile != null && !imgFile.isEmpty()){
            FileDetail file = fileService.upload("checklist/image", imgFile);
            checkList.setFile(file);
        }

        //checkList 기본 정보 수정
        checkList.updateInfo(requestDto, subCategory);
    }

    @Transactional
    public void addStepItem(CreateStepRequestDto requestDto, CheckList checkList) {
        for (StepRequestDto dto : requestDto.getSteps()) {
            Step step = Step.createEntity(dto, checkList);
            checkList.addStep(step);
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
