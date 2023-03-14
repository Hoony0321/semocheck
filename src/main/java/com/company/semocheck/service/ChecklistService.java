package com.company.semocheck.service;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.domain.*;
import com.company.semocheck.domain.request.checklist.*;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.ChecklistRepository;
import com.company.semocheck.repository.StepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final CategoryService categoryService;
    private final FileService fileService;
    private final StepRepository stepRepository;

    public Checklist findById(Long id){
        Optional<Checklist> findOne = checklistRepository.findById(id);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, "해당 id의 체크리스트는 존재하지 않습니다.");

        return findOne.get();
    }
    public List<Checklist> getAllChecklist(){
         return checklistRepository.findAll();
    }
    public List<Checklist> getPublishedChecklistByQuery(String categoryMain, String categorySub, String title, String owner) {

        List<Checklist> checklists = checklistRepository.findByTemporaryIsNullAndPublishIsTrue();

        //Category MainName
        if(categoryMain != null){
            checklists = checklists.stream()
                    .filter(chk -> chk.getCategory().getMainCategory().getName().equals(categoryMain))
                    .collect(Collectors.toList());
        }

        //Category SubName
        if(categorySub != null){
            checklists = checklists.stream()
                    .filter(chk -> chk.getCategory().getName().equals(categorySub))
                    .collect(Collectors.toList());
        }

        //Checklist title
        if(title != null){
            checklists = checklists.stream()
                    .filter(chk -> chk.getTitle().contains(title))
                    .collect(Collectors.toList());
        }

        //Checklist ownerName
        if(owner != null){
            checklists = checklists.stream()
                    .filter(chk -> chk.getOwner().getName().equals(owner))
                    .collect(Collectors.toList());
        }

        return checklists;
    }

    public List<Checklist> getMemberChecklistsByQuery(Member member, String categoryMain, String categorySub, String title, Boolean published, Boolean completed, Boolean owner) {
        List<Checklist> checklists = checklistRepository.findByOwnerAndTemporaryIsNull(member);

        //Category MainName
        if(categoryMain != null){
            checklists = checklists.stream()
                    .filter(chk -> chk.getCategory().getMainCategory().getName().equals(categoryMain))
                    .collect(Collectors.toList());
        }

        //Category SubName
        if(categorySub != null){
            checklists = checklists.stream()
                    .filter(chk -> chk.getCategory().getName().equals(categorySub))
                    .collect(Collectors.toList());
        }

        //Checklist title
        if(title != null){
            checklists = checklists.stream()
                    .filter(chk -> chk.getTitle().contains(title))
                    .collect(Collectors.toList());
        }

        //Checklist publish
        if(published != null){
            checklists = checklists.stream()
                    .filter(chk -> chk.getPublish() == published)
                    .collect(Collectors.toList());
        }

        //Checklist completed
        if(completed != null){
            checklists = checklists.stream()
                    .filter(chk -> chk.getComplete() == completed)
                    .collect(Collectors.toList());
        }

        //Checklist owner
        if(owner != null){
            if(owner){ //자신의 체크리스트만 리스트
                checklists = checklists.stream()
                        .filter(chk -> chk.getOrigin() == null)
                        .collect(Collectors.toList());
            }
            else{
                checklists = checklists.stream()
                        .filter(chk -> chk.getOrigin() != null)
                        .collect(Collectors.toList());
            }
        }

        return checklists;
    }

    public List<Checklist> getRecommendChecklist(Member member) {

        List<Checklist> checklists = checklistRepository.findByTemporaryIsNullAndPublishIsTrue();

        //filtering checklist by member's category
        List<SubCategory> subCategories = new ArrayList<>();
        for (MemberCategory memberCategory : member.getCategories()) {
            SubCategory subCategory = memberCategory.getSubCategory();
            subCategories.add(subCategory);
        }

        checklists = checklists.stream()
                .filter(chk -> subCategories.contains(chk.getCategory()))
                .collect(Collectors.toList());

        return checklists;

    }

    public List<Checklist> getPopularChecklist() {
        //TODO : temporary / publish false 빼고 조회하도록 변경.
        List<Checklist> checklists = checklistRepository.findByTemporaryIsNullAndPublishIsTrue();

        checklists.sort(Comparator.comparing(Checklist::getViewCount));
        checklists = checklists.stream().limit(10).collect(Collectors.toList());

        return checklists;
    }

    public List<Checklist> getSimilarChecklist(Checklist checklist) {
        //TODO : 카테고리 말고도 그 외 정보를 토대로 조회하도록 변경
        List<Checklist> checklists = checklistRepository.findByTemporaryIsNullAndPublishIsTrue();

        checklists = checklists.stream().filter(chk -> chk.getCategory().equals(checklist.getCategory()))
                                        .limit(5)
                                        .collect(Collectors.toList());

        return checklists;
    }

    @Transactional
    public Long createChecklist(CreateChecklistRequestDto requestDto, Member member){
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

        //image file
        if(requestDto.getFileId() != null){
            FileDetail fileDetail = fileService.findById(requestDto.getFileId());
            checklist.setFile(fileDetail);
        }

        if(checklist.getTemporary() == null && checklist.getDefaultImage() == null && checklist.getFileDetail() == null){
            throw new GeneralException(Code.BAD_REQUEST, "이미지가 설정되지 않았습니다.");
        }

        //Checklist 저장
        checklistRepository.save(checklist);

        return checklist.getId();
    }

    @Transactional
    public void adminDeleteChecklist(Checklist checklist, Member member) {
        if(checklist.getOwner().equals(member)) member.removeChecklist(checklist);
        checklistRepository.delete(checklist);
    }

    @Transactional
    public void deleteChecklist(Checklist checklist, Member member) {
        member.removeChecklist(checklist);
        checklistRepository.delete(checklist);
    }

    @Transactional
    public void updateChecklist(Checklist checklist, UpdateChecklistRequestDto requestDto) {
        //check origin checklist validation
        if(checklist.getOrigin() != null && requestDto.getPublish()){ //origin checklist가 존재하면 publish true 불가능.
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

        //checklist 기본 정보 수정
        checklist.updateInfo(requestDto, subCategory);

        //image 설정
        if(requestDto.getFileId() != null){
            FileDetail newImageFile = fileService.findById(requestDto.getFileId());
            checklist.setFile(newImageFile);
        }

        else{ checklist.setFile(null); }

        if(checklist.getTemporary() == null && checklist.getDefaultImage() == null && checklist.getFileDetail() == null){
            throw new GeneralException(Code.BAD_REQUEST, "이미지가 설정되지 않았습니다.");
        }

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

        //이미지 설정
        if(existedChecklist.getFileDetail() != null){
            FileDetail fileDetail = fileService.copy(existedChecklist.getFileDetail());
            checklist.setFile(fileDetail);
        }

        if(checklist.getDefaultImage() == null && checklist.getFileDetail() == null){
            throw new GeneralException(Code.BAD_REQUEST, "이미지가 설정되지 않았습니다.");
        }

        //Checklist 저장
        checklistRepository.save(checklist);



        return checklist.getId();
    }

    @Transactional
    public void increaseViewCount(Checklist checklist) {
        checklist.increaseViewCount();
    }

    public List<Checklist> sortChecklists(List<Checklist> checklists, String sort, String direction) {
        if (sort.equals("date")) {
            if (direction.equals("asc")) {
                checklists.sort(Comparator.comparing(Checklist::getModifiedDate));
            } else {
                checklists.sort(Comparator.comparing(Checklist::getModifiedDate).reversed());
            }
        }
        else if (sort.equals("view")) {
            if (direction.equals("asc")) {
                checklists.sort(Comparator.comparing(Checklist::getViewCount));
            } else {
                checklists.sort(Comparator.comparing(Checklist::getViewCount).reversed());
            }
        }
        else if (sort.equals("scrap")) {
            if (direction.equals("asc")) {
                checklists.sort(Comparator.comparing(Checklist::getScrapCount));
            } else {
                checklists.sort(Comparator.comparing(Checklist::getScrapCount).reversed());
            }
        }

        return checklists;
    }


    public List<Checklist> getMemberTempChecklist(Member member) {
        List<Checklist> checklists = checklistRepository.findByOwnerAndTemporaryIsNotNull(member);
        return checklists;
    }

    @Transactional
    public Checklist getPublishedChecklistById(Long checklistId) {
        //Get checklist by id
        Checklist checklist = findById(checklistId);
        if(!checklist.getPublish()) throw new GeneralException(Code.NOT_FOUND, "해당 id의 체크리스트는 존재하지 않습니다.");

        //increase view count
        checklist.increaseViewCount();

        return checklist;
    }

    @Transactional
    public void updateChecklistByViewer(Checklist checklist, Member member) {
        checklist.updateInfoByViewer(member);
    }
}
