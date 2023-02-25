package com.company.semocheck.service;

import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.Step;
import com.company.semocheck.domain.dto.request.checklist.CreateChecklistRequestDto;
import com.company.semocheck.domain.dto.request.checklist.StepRequestDto;
import com.company.semocheck.domain.dto.request.checklist.UpdateChecklistRequestDto;
import com.company.semocheck.repository.ChecklistRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChecklistServiceTest {
    @Autowired private ChecklistService checklistService;
    @Autowired private MemberService memberService;

    @Autowired private ChecklistRepository checklistRepository;

    private Member member;

    @BeforeEach
    public void before(){
        final Long ADMIN_ID = 1L;
        member = memberService.findById(ADMIN_ID);
    }


    @Test
    @Transactional
    public void 체크리스트_생성() throws Exception {
        //given
        CreateChecklistRequestDto requestDto = CreateChecklistRequestDto.builder()
                .title("test title")
                .brief("test brief").build();

        //when
        Long checklist_id = checklistService.createChecklist(requestDto, member);

        //then
        Checklist checklist = checklistRepository.findById(checklist_id).get();

        assertThat(checklist.getOwner()).isEqualTo(member); // create 확인
        assertThat(checklist.getTitle()).isEqualTo("test title"); // 정보 확인

        assertDoesNotThrow(()->{ // 연관관계 확인
            Optional<Checklist> findOne = member.getChecklists().stream().filter((chk -> chk == checklist)).findAny();
            if(findOne.isEmpty()) throw new RuntimeException("회원 체크리스트 목록에 추가되지 않음.");
        });

    }

    @Test
    @Transactional
    public void 체크리스트_생성_TITLE_IS_NULL() throws Exception {
        //given
        CreateChecklistRequestDto requestDto = CreateChecklistRequestDto.builder()
                .title(null)
                .brief("test brief").build();
        //when

        //then
        assertThrows(ConstraintViolationException.class, () -> {
            Long checklist_id = checklistService.createChecklist(requestDto, member);
        });
    }

    @Test
    @Transactional
    public void 체크리스트_생성_TITLE_EXCEED_MAX_SIZE() throws Exception {
        //given
        CreateChecklistRequestDto requestDto = CreateChecklistRequestDto.builder()
                .title("test brief test brief test brief test brief test brief test brief test brief test brief test brief test brief test brief test brief test brief")
                .brief("test brief").build();
        //when

        //then
        assertThrows(ConstraintViolationException.class, () -> {
            Long checklist_id = checklistService.createChecklist(requestDto, member);
        });
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void 체크리스트_수정() throws Exception {
        //given
        Long checklistId = createChecklist();
        Checklist checklist = checklistService.findById(checklistId);
        List<StepRequestDto> steps = new ArrayList<>();
        for (Step step : checklist.getSteps()) {
            System.out.println(step.getStepOrder());
            StepRequestDto stepDto = StepRequestDto.builder()
                    .stepId(step.getId())
                    .name(step.getName() + " modified")
                    .order(step.getStepOrder()).build();
            steps.add(stepDto);
        }

        UpdateChecklistRequestDto requestDto = UpdateChecklistRequestDto.builder()
                .title("modified title")
                .brief("modified brief")
                .publish(true)
                .mainCategoryName("생활")
                .subCategoryName("결혼")
                .fileId(null)
                .steps(steps).build();

        //when
        checklistService.updateChecklist(checklist, requestDto);
        Checklist modified = checklistService.findById(checklistId);

        //then
        assertThat(modified.getTitle()).isEqualTo("modified title");
    }

    @Test
    @Transactional
    public void 체크리스트_수정_TITLE_EXCEED_MAX_SIZE() throws Exception {
        //given
        Long checklistId = createChecklist();
        Checklist checklist = checklistService.findById(checklistId);
        List<StepRequestDto> steps = new ArrayList<>();
        for (Step step : checklist.getSteps()) {
            System.out.println(step.getStepOrder());
            StepRequestDto stepDto = StepRequestDto.builder()
                    .stepId(step.getId())
                    .name(step.getName() + " modified")
                    .order(step.getStepOrder()).build();
            steps.add(stepDto);
        }

        //when
        UpdateChecklistRequestDto requestDto = UpdateChecklistRequestDto.builder()
                .title("modified title modified title modified title modified title modified title modified title modified title modified title modified title modified title modified title modified title")
                .brief("modified brief")
                .publish(true)
                .mainCategoryName("생활")
                .subCategoryName("결혼")
                .fileId(null)
                .steps(steps).build();

        //then
        checklistService.updateChecklist(checklist, requestDto);
    }

    private Long createChecklist(){
        List<StepRequestDto> steps = new ArrayList<>();
        for(int i = 1; i <= 5; i++){
            StepRequestDto _step = StepRequestDto.builder()
                    .name("step1")
                    .order(i).build();
            steps.add(_step);
        }

        CreateChecklistRequestDto requestDto = CreateChecklistRequestDto.builder()
                .title("test title")
                .brief("test brief, test brief, test brief, test brief, test brief,")
                .mainCategoryName("생활")
                .subCategoryName("인테리어")
                .publish(false)
                .steps(steps)
                .build();

        Long checklistId = checklistService.createChecklist(requestDto, member);
        return checklistId;
    }


}