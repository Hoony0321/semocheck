package com.company.semocheck.service.checklist;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.domain.*;
import com.company.semocheck.domain.category.SubCategory;
import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.request.checklist.StepRequestDto;
import com.company.semocheck.domain.request.tempChecklist.CreateTempChecklistRequest;
import com.company.semocheck.domain.request.tempChecklist.UpdateTempChecklistRequest;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.ChecklistRepository;
import com.company.semocheck.repository.StepRepository;
import com.company.semocheck.service.CategoryService;
import com.company.semocheck.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TempChecklistService {

    private final ChecklistRepository checklistRepository;
    private final CategoryService categoryService;
    private final FileService fileService;
    private final StepRepository stepRepository;

    public Checklist findById(Long id) {
        Optional<Checklist> findOne = checklistRepository.findTempChecklistById(id);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_CHECKLIST);
        return findOne.get();
    }

    @Transactional
    public Long createChecklist(CreateTempChecklistRequest requestDto, Member member) {
        SubCategory category = null;

        //Category Entity 찾기
        if(requestDto.getMainCategoryName() != null && requestDto.getSubCategoryName() != null){
            category = categoryService.findSubCategoryByName(requestDto.getMainCategoryName(), requestDto.getSubCategoryName());
        }
        else if(requestDto.getSubCategoryName() != null) {
            throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.NOT_FOUND_CATEGORY);
        }

        //Checklist 생성
        Checklist checklist = Checklist.createTempEntity(requestDto, member, category);

        //image file
        if(requestDto.getImageId() != null){
            FileDetail fileDetail = fileService.findById(requestDto.getImageId());
            checklist.setImage(fileDetail);
        } else if (requestDto.getDefaultImageId() != null) {
            FileDetail fileDetail = fileService.findById(requestDto.getDefaultImageId());
            checklist.setDefaultImage(fileDetail);
        }

        //Checklist 저장
        checklistRepository.save(checklist);

        return checklist.getId();

    }

    @Transactional
    public void updateChecklist(Checklist checklist, UpdateTempChecklistRequest requestDto) {
        //check origin checklist validation
        if(checklist.getOrigin() != null && requestDto.getPublish()){ //origin checklist가 존재하면 publish true 불가능.
            throw new GeneralException(Code.FORBIDDEN, ErrorMessages.NOT_PUBLISHED);
        }

        //category validation
        SubCategory subCategory = null;
        if(requestDto.getMainCategoryName() != null && requestDto.getSubCategoryName() != null) {
            subCategory = categoryService.findSubCategoryByName(requestDto.getMainCategoryName(), requestDto.getSubCategoryName());
        }

        //TODO : requestDto에 만약 없는 stepId가 있을 경우 error throw
        if(requestDto.getSteps() != null){
            //step validation
            List<Integer> orderList = new ArrayList<>();
            requestDto.getSteps().stream().forEach(s -> {
                if(s.getStepId() == null || s.getName() == null) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.INVALID_STEP);
                orderList.add(s.getOrder());
            });
            Collections.sort(orderList);
            for(int i = 1; i <= orderList.size(); i++){
                if(!orderList.get(i-1).equals(i)) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.INVALID_STEP);
            }

            //step update & add & delete
            List<Boolean> modifedList = new ArrayList<Boolean>();
            checklist.getSteps().stream().forEach(_s -> modifedList.add(false));

            for (StepRequestDto step : requestDto.getSteps()) {
                if(step.getStepId() == -1){ //add new stepp
                    Step stepEntity = Step.createEntity(step, checklist);
                    checklist.addStep(stepEntity);
                }
                else{
                    Optional<Step> findOne = checklist.getSteps().stream().filter(_step -> step.getStepId().equals(_step.getId())).findFirst();
                    if(findOne.isPresent()){
                        findOne.get().update(step);
                        int updateIdx = checklist.getSteps().indexOf(findOne.get());
                        modifedList.set(updateIdx, true);
                    } // existed step info update
                    else throw new GeneralException(Code.NOT_FOUND, ErrorMessages.INVALID_STEP); // not found step id
                }
            }

            for(int i = 0; i < modifedList.size(); i++){ //정보가 없는 스텝 삭제
                if(modifedList.get(i) == null || !modifedList.get(i)){ //null이거나 false인 경우
                    Step step = checklist.getSteps().get(i);
                    checklist.removeStep(step);
                    stepRepository.delete(step);
                }
            }
        }

        //checklist 기본 정보 수정
        checklist.updateTempInfo(requestDto, subCategory);

        //image 설정
        if(requestDto.getImageId() != null){
            FileDetail newImageFile = fileService.findById(requestDto.getImageId());
            checklist.setImage(newImageFile);
        }else{
            checklist.setImage(null);
        }
        if(requestDto.getDefaultImageId() != null){
            FileDetail newDefaultImageFile = fileService.findById(requestDto.getDefaultImageId());
            checklist.setDefaultImage(newDefaultImageFile);
        }else{
            checklist.setDefaultImage(null);
        }
    }

    @Transactional
    public void deleteChecklist(Checklist checklist, Member member) {
        member.removeChecklist(checklist);
        checklistRepository.delete(checklist);
    }
}
