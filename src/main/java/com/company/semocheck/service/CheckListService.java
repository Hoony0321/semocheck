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

        //TODO : requestDto에 만약 없는 stepId가 있을 경우 error throw
        if(requestDto.getSteps() != null){
            //step validation
            List<Integer> orderList = new ArrayList<>();
            requestDto.getSteps().stream().forEach(s -> orderList.add(s.getOrder()));
            Collections.sort(orderList);
            for(int i = 1; i <= orderList.size(); i++){
                if(!orderList.get(i-1).equals(i)) throw new GeneralException(Code.BAD_REQUEST, "스텝의 순서 오류");
            }

            //step update & add & delete
            List<Boolean> modifedList = new ArrayList<Boolean>();
            checkList.getSteps().stream().forEach(_s -> modifedList.add(false));

            for (StepRequestDto step : requestDto.getSteps()) {
                if(step.getStepId() == -1){ //add new stepp
                    Step stepEntity = Step.createEntity(step, checkList);
                    checkList.addStep(stepEntity);
                }
                else{
                    Optional<Step> findOne = checkList.getSteps().stream().filter(_step -> step.getStepId().equals(_step.getId())).findFirst();
                    if(findOne.isPresent()){
                        findOne.get().update(step);
                        int updateIdx = checkList.getSteps().indexOf(findOne.get());
                        modifedList.set(updateIdx, true);
                    } // existed step info update
                    else throw new GeneralException(Code.NOT_FOUND, "not found step id - " + step.getStepId()); // not found step id
                }
            }

            for(int i = 0; i < modifedList.size(); i++){ //정보가 없는 스텝 삭제
                if(modifedList.get(i) == null || !modifedList.get(i)){ //null이거나 false인 경우
                    Step step = checkList.getSteps().get(i);
                    checkList.removeStep(step);
                    stepRepository.delete(step);
                }
            }
        }

        //image file update
        if(!imgFile.isEmpty()){
            FileDetail file = fileService.upload("checklist/image", imgFile);
            checkList.setFile(file);
        }else{
            checkList.setFile(null);
        }

        //checkList 기본 정보 수정
        checkList.updateInfo(requestDto, subCategory);
    }



    @Transactional
    public void updateStepProgress(CheckList checkList, UpdateStepRequestDto requestDto) {
        //step progress update
        for (StepUpdateDto step : requestDto.getSteps()) {
            Optional<Step> findOne = checkList.getSteps().stream().filter(_step -> step.getStepId().equals(_step.getId())).findFirst();
            if(findOne.isPresent()){ findOne.get().update(step); } // existed step info update
            else throw new GeneralException(Code.NOT_FOUND, "not found step id - " + step.getStepId()); // not found step id
        }

        //checkList progress 수정
        checkList.updateProgress();
    }

    public List<CheckList> findAllVisible() {
        List<CheckList> checkLists = new ArrayList<>();
        for (CheckList checkList : checkListRepository.findAll()) {
            if(checkList.getVisibility()) checkLists.add(checkList);
        }

        return checkLists;
    }
}
