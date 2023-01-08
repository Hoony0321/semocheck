package com.company.semocheck.service;

import com.company.semocheck.domain.CheckList;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.dto.request.checkList.CreateCheckListRequestDto;
import com.company.semocheck.repository.CheckListRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CheckListServiceTest {

    @Autowired private CheckListService checkListService;
    @Autowired private MemberService memberService;

    @Autowired private CheckListRepository checkListRepository;

    private final Long ADMIN_ID = 1L;


    @Test
    @Transactional
    public void 체크리스트_생성() throws Exception {
        //given
        Member member = memberService.findById(ADMIN_ID);
        CreateCheckListRequestDto requestDto = CreateCheckListRequestDto.builder()
                .title("test title")
                .brief("test brief").build();

        //when
        Long checkList_id = checkListService.createCheckList(requestDto, member);

        //then
        CheckList checkList = checkListRepository.findById(checkList_id).get();

        assertThat(checkList.getOwner()).isEqualTo(member); // create 확인
        assertThat(checkList.getTitle()).isEqualTo("test title"); // 정보 확인

        assertDoesNotThrow(()->{ // 연관관계 확인
            Optional<CheckList> findOne = member.getCheckLists().stream().filter((chk -> chk == checkList)).findAny();
            if(findOne.isEmpty()) throw new RuntimeException("회원 체크리스트 목록에 추가되지 않음.");
        });

    }

}