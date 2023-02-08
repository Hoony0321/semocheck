package com.company.semocheck.service;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.domain.*;
import com.company.semocheck.domain.dto.request.checklist.*;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.ChecklistRepository;
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
public class ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final CategoryService categoryService;
    private final FileService fileService;
    private final StepRepository stepRepository;

    public List<Checklist> getAllVisibleChecklists() {
        List<Checklist> checklists = new ArrayList<>();
        for (Checklist checklist : checklistRepository.findAll()) {
            if(checklist.getVisibility()) checklists.add(checklist);
        }
        return checklists;
    }

    public List<Checklist> getAllMemberChecklistsInProgress(Member member) {
        List<Checklist> checklistsInProgresses = new ArrayList<>();
        for (Checklist checklist : member.getChecklists()) {
            if(!checklist.getComplete()) checklistsInProgresses.add(checklist);
        }

        return checklistsInProgresses;
    }

    public List<Checklist> getAllMemberChecklistsInComplete(Member member) {
        List<Checklist> checklistsInComplete = new ArrayList<>();
        for (Checklist checklist : member.getChecklists()) {
            if(checklist.getComplete()) checklistsInComplete.add(checklist);
        }

        return checklistsInComplete;
    }

    public List<Checklist> getAllMemberChecklistsMadeByMember(Member member) {
        List<Checklist> checklistsInComplete = new ArrayList<>();
        for (Checklist checklist : member.getChecklists()) {
            if(checklist.getOrigin() == null) checklistsInComplete.add(checklist);
        }

        return checklistsInComplete;
    }

    public Checklist findById(Long id){
        Optional<Checklist> findOne = checklistRepository.findById(id);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, "해당 id의 체크리스트는 존재하지 않습니다.");

        return findOne.get();
    }

    public List<Checklist> findByCategoryIn(Member member) {
        //set sub category list
        List<SubCategory> subCategories = new ArrayList<>();
        for (MemberCategory memberCategory : member.getCategories()) {
            SubCategory subCategory = memberCategory.getSubCategory();
            subCategories.add(subCategory);
        }

        List<Checklist> checklists = checklistRepository.findByCategoryIn(subCategories);
        return checklists;
    }

    @Transactional
    public Long createChecklist(CreateChecklistRequestDto requestDto, Member member, MultipartFile imgFile){
        SubCategory category = null;

        //Category Entity 찾기
        if(requestDto.getMainCategoryName() != null && requestDto.getSubCategoryName() != null){
            category = categoryService.findSubCategoryByName(requestDto.getMainCategoryName(), requestDto.getSubCategoryName());
        }
        else if(requestDto.getSubCategoryName() != null) {
            throw new GeneralException(Code.BAD_REQUEST, "1차 카테고리가 정의되지 않았습니다.");
        }

        //Checklist 생성
        Checklist checklist = Checklist.createEntity(requestDto, member, category);

        //Image File 설정
        if(imgFile != null && !imgFile.isEmpty()){
            FileDetail file = fileService.upload("checklist/image", imgFile);
            checklist.setFile(file);
        }

        //Checklist 저장
        checklistRepository.save(checklist);

        return checklist.getId();
    }

    @Transactional
    public void deleteChecklist(Checklist checklist, Member member) {
        member.removeChecklist(checklist);
        checklistRepository.delete(checklist);
    }

    @Transactional
    public void updateChecklist(Checklist checklist, UpdateChecklistRequestDto requestDto, MultipartFile imgFile) {
        //check origin checklist validation
        if(checklist.getOrigin() != null && requestDto.getVisibility()){ //origin checklist가 존재하면 visibility true 불가능.
            throw new GeneralException(Code.FORBIDDEN, "해당 체크리스트는 공개할 수 없습니다.");
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
            requestDto.getSteps().stream().forEach(s -> orderList.add(s.getOrder()));
            Collections.sort(orderList);
            for(int i = 1; i <= orderList.size(); i++){
                if(!orderList.get(i-1).equals(i)) throw new GeneralException(Code.BAD_REQUEST, "스텝의 순서 오류");
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
                    else throw new GeneralException(Code.NOT_FOUND, "not found step id - " + step.getStepId()); // not found step id
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

        //image file update
        if(!imgFile.isEmpty()){
            FileDetail file = fileService.upload("checklist/image", imgFile);
            checklist.setFile(file);
        }else{
            checklist.setFile(null);
        }

        //checklist 기본 정보 수정
        checklist.updateInfo(requestDto, subCategory);
    }



    @Transactional
    public void updateStepProgress(Checklist checklist, UpdateStepRequestDto requestDto) {
        //step progress update
        for (StepUpdateDto step : requestDto.getSteps()) {
            Optional<Step> findOne = checklist.getSteps().stream().filter(_step -> step.getStepId().equals(_step.getId())).findFirst();
            if(findOne.isPresent()){ findOne.get().update(step); } // existed step info update
            else throw new GeneralException(Code.NOT_FOUND, "not found step id - " + step.getStepId()); // not found step id
        }

        //checklist progress 수정
        checklist.updateProgress();
    }

    @Transactional
    public Long useChecklist(Checklist existedChecklist, Member member) {
        //체크리스트 생성
        Checklist checklist = Checklist.createEntity(existedChecklist, member); //기존 체크리스트 정보를 토대로 새로운 체크리스트 생성

        //Checklist 저장
        checklistRepository.save(checklist);

        return checklist.getId();
    }
}
