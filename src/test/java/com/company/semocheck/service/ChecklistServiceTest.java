package com.company.semocheck.service;

import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.repository.ChecklistRepository;
import com.company.semocheck.service.checklist.ChecklistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ChecklistServiceTest {

    @Autowired private ChecklistService checklistService;
    @Autowired private ChecklistRepository checklistRepository;

    @Test
    @Transactional
    public void 체크리스트_생성() throws Exception {
        //given
//        List<StepRequestDto>
//        CreateChecklistRequest.builder()
//                .title("테스트 체크리스트 제목")
//                .brief("테스트 체크리스트 설명")
//                .mainCategoryName("생활")
//                .subCategoryName("부동산")
//                .publish(true)
//                .steps()


        //when

        //then
    }

}