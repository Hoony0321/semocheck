package com.company.semocheck.service.checklist;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.domain.*;
import com.company.semocheck.domain.category.SubCategory;
import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.dto.category.SubCategoryDetailDto;
import com.company.semocheck.domain.dto.checklist.ChecklistPostSimpleDto;
import com.company.semocheck.domain.dto.checklist.HomeChecklistDto;
import com.company.semocheck.form.CreateChecklistForm;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.member.MemberCategory;
import com.company.semocheck.domain.request.checklist.*;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.form.CreateStepForm;
import com.company.semocheck.repository.*;
import com.company.semocheck.service.CategoryService;
import com.company.semocheck.service.FileService;
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
    private final StepRepository stepRepository;

    private final ScrapRepository scrapRepository;
    private final ReportRepository reportRepository;

    private final CategoryService categoryService;
    private final FileService fileService;


    //transactional method
    public Checklist findById(Long id){
        Optional<Checklist> findOne = checklistRepository.findChecklistById(id);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_CHECKLIST);

        return findOne.get();
    }

    //transactional method
    public List<Checklist> findAllChecklists(){
        return checklistRepository.findAllChecklist();
    }

    //service method
    public List<Checklist> queryPublishedChecklist(String categoryMain, String categorySub, String title, String owner) {
        List<Checklist> checklists = checklistRepository.findChecklistIsPublished();

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
            checklists.stream().filter(chk -> (chk.getOwner().getName() != null && chk.getOwner().getName().equals(owner)))
                    .collect(Collectors.toList());
        }

        return checklists;
    }

    public List<Checklist> queryMemberChecklists(Member member, String categoryMain, String categorySub, String title, Boolean published, Boolean completed, Boolean owner) {
        List<Checklist> checklists = checklistRepository.findChecklistByOwner(member);

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
                    .filter(chk -> chk.getUsageInfo().getComplete() == completed)
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
        List<Checklist> checklists = checklistRepository.findChecklistIsPublished();

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

    public List<Checklist> getPopularChecklist(String categoryMain, String categorySub) {
        List<Checklist> checklists = checklistRepository.findChecklistIsPublished();
        if(categoryMain == null || categorySub == null){ //전체 카테고리에서 조회
            checklists.sort(Comparator.comparing(chk -> chk.getStatsInfo().getViewCount()));
            checklists = checklists.stream().limit(10).collect(Collectors.toList());
        }
        else{ //카테고리별 조회
            checklists = checklists.stream()
                    .filter(chk -> chk.getCategory().getMainCategory().getName().equals(categoryMain))
                    .filter(chk -> chk.getCategory().getName().equals(categorySub))
                    .sorted(Comparator.comparing(chk -> chk.getStatsInfo().getViewCount()))
                    .limit(10)
                    .collect(Collectors.toList());
        }
        return checklists;
    }

    public List<Checklist> getSimilarChecklist(Checklist checklist) {
        //TODO : 카테고리 말고도 그 외 정보를 토대로 조회하도록 변경
        List<Checklist> checklists = checklistRepository.findChecklistIsPublished();

        checklists = checklists.stream().filter(chk -> chk.getCategory().equals(checklist.getCategory()))
                                        .limit(5)
                                        .collect(Collectors.toList());

        return checklists;
    }

    @Transactional
    public Long createChecklist(CreateChecklistForm form, Member member){
        SubCategory category = null;

        //Category Entity 찾기
        if(form.getMainCategoryName() != null && form.getSubCategoryName() != null){
            category = categoryService.findSubCategoryByName(form.getMainCategoryName(), form.getSubCategoryName());
        }
        else if(form.getSubCategoryName() != null) {
            throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.NOT_FOUND_CATEGORY);
        }

        //Checklist 생성
        Checklist checklist = Checklist.createEntity(form, member, category);

        //image setting
        if(form.getImageId() != null){
            FileDetail fileDetail = fileService.findById(form.getImageId());
            checklist.setImage(fileDetail);
        } else if(form.getDefaultImageId() != null){
            FileDetail fileDetail = fileService.findById(form.getDefaultImageId());
            checklist.setDefaultImage(fileDetail);
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

        member.removeChecklist(checklist); // 회원 체크리스트 배열에서 삭제
        scrapRepository.deleteAll(scrapRepository.findByChecklist(checklist));// 해당 체크리스트와 연관된 스크랩 삭제
        reportRepository.deleteAll(reportRepository.findByChecklist(checklist));// 해당 체크리스트와 연관된 신고 삭제

        List<Checklist> copiedChecklists = checklistRepository.findCopiedChecklistByChecklist(checklist);// 해당 체크리스트를 복사한 체크리스트들을 가져옴
        for(Checklist copiedChecklist : copiedChecklists){
            copiedChecklist.setOrigin(null);
        }

        checklistRepository.delete(checklist);
    }

    @Transactional
    public void updateChecklist(Checklist checklist, UpdateChecklistRequestDto requestDto) {
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
                    Step stepEntity = Step.createEntity(
                            CreateStepForm.builder().stepName(step.getName()).stepDescription(step.getDescription()).build(),
                            checklist,
                            step.getOrder());
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
        checklist.updateInfo(requestDto, subCategory);

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
    public void updateStepProgress(Checklist checklist, UpdateStepRequestDto requestDto) {
        //step progress update
        for (StepUpdateDto step : requestDto.getSteps()) {
            if(step.getStepId() == null || step.getIsCheck() == null) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.INVALID_STEP);
            Optional<Step> findOne = checklist.getSteps().stream().filter(_step -> step.getStepId().equals(_step.getId())).findFirst();
            if(findOne.isPresent()){ findOne.get().update(step.getIsCheck()); } // existed step info update
            else throw new GeneralException(Code.NOT_FOUND, ErrorMessages.INVALID_STEP); // not found step id
        }

        //checklist progress 수정
        checklist.updateProgress();
    }

    @Transactional
    public Long useChecklist(Checklist existedChecklist, Member member) {
        //중복된 체크리스트 정보 확인
        member.getChecklists().stream().forEach(checklist -> {
            if(checklist.getOrigin() != null && checklist.getOrigin().getId().equals(existedChecklist.getId())){
                throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.ALREADY_USED_CHECKLIST);
            }
        });

        //체크리스트 생성
        Checklist checklist = Checklist.createCopyEntity(existedChecklist, member); //기존 체크리스트 정보를 토대로 새로운 체크리스트 생성

        //이미지 설정
        if(existedChecklist.getImage() != null){
            FileDetail fileDetail = fileService.copy(existedChecklist.getImage());
            checklist.setImage(fileDetail);
        } else if (existedChecklist.getDefaultImage() != null) {
            FileDetail fileDetail = fileService.copy(existedChecklist.getDefaultImage());
            checklist.setImage(fileDetail);
        }

        //Checklist 저장
        checklistRepository.save(checklist);

        return checklist.getId();
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
                checklists.sort(Comparator.comparing(chk -> chk.getStatsInfo().getViewCount()));
            } else {
                checklists.sort(Comparator.comparing((Checklist chk) -> chk.getStatsInfo().getViewCount()).reversed());
            }
        }
        else if (sort.equals("scrap")) {
            if (direction.equals("asc")) {
                checklists.sort(Comparator.comparing(chk -> chk.getStatsInfo().getScrapCount()));
            } else {
                checklists.sort(Comparator.comparing((Checklist chk) -> chk.getStatsInfo().getScrapCount()).reversed());
            }
        }

        return checklists;
    }

    @Transactional
    public Checklist getPublishedChecklistById(Long checklistId) {
        //Get checklist by id
        Checklist checklist = findById(checklistId);
        if(!checklist.getPublish()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_CHECKLIST);

        return checklist;
    }

    @Transactional
    public void updateChecklistStatsByViewer(Checklist checklist, Optional<Member> member) {
        checklist.updateStatsInfoByViewer(member);
        checklist.getCategory().increaseCount();
        checklist.getCategory().getMainCategory().increaseCount();
    }

    @Transactional
    public void restartProgress(Checklist checklist) {
        // isCheck reset
        checklist.getSteps().stream().forEach(step -> step.update(false));
        // progress reset
        checklist.updateProgress();
    }

    public List<HomeChecklistDto> getHomeContent(Optional<Member> member) {
        final SubCategory category1 = categoryService.findSubCategoryByName("생활", "결혼");
        final SubCategory category2 = categoryService.findSubCategoryByName("생활", "인테리어");

        List<Checklist> checklists1 = new ArrayList<>();
        List<Checklist> checklists2 = new ArrayList<>();

        List<Checklist> allChecklists = checklistRepository.findAllChecklist();
        checklists1 = allChecklists.stream().filter(chk -> chk.getCategory().equals(category1)).collect(Collectors.toList()).subList(0,2);
        checklists2 = allChecklists.stream().filter(chk -> chk.getCategory().equals(category2)).collect(Collectors.toList()).subList(0,2);

        List<HomeChecklistDto> homeChecklistDtos = new ArrayList<>();

        homeChecklistDtos.add(new HomeChecklistDto(SubCategoryDetailDto.createDto(category1), ChecklistPostSimpleDto.createDtos(checklists1, member)));
        homeChecklistDtos.add(new HomeChecklistDto(SubCategoryDetailDto.createDto(category2), ChecklistPostSimpleDto.createDtos(checklists2, member)));

        return homeChecklistDtos;
    }
}
