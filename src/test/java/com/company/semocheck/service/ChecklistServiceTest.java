package com.company.semocheck.service;

import com.company.semocheck.common.TestCategoryUtils;
import com.company.semocheck.common.TestChecklistUtils;
import com.company.semocheck.common.TestMemberUtils;
import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.checklist.ChecklistStats;
import com.company.semocheck.domain.checklist.ChecklistType;
import com.company.semocheck.domain.checklist.ChecklistUsage;
import com.company.semocheck.domain.dto.step.StepDto;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.request.checklist.CreateChecklistRequest;
import com.company.semocheck.domain.request.checklist.StepRequestDto;
import com.company.semocheck.repository.ChecklistRepository;
import com.company.semocheck.service.checklist.ChecklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ChecklistServiceTest {

    @Autowired private ChecklistService checklistService;
    @Autowired private ChecklistRepository checklistRepository;
    @Autowired private MemberService memberService;

    @Autowired private TestMemberUtils testMemberUtils;
    @Autowired private TestCategoryUtils testCategoryUtils;
    @Autowired private TestChecklistUtils testChecklistUtils;

    private Member member;

    @BeforeEach
    public void before(){
        testCategoryUtils.initCategory();
        Long memberId = testMemberUtils.createMember();
        member = memberService.findById(memberId);
    }

    @Test
    @Transactional
    public void 체크리스트_생성() throws Exception {
        //given
        List<StepRequestDto> stepDtos = new ArrayList<>();
        stepDtos.add(StepRequestDto.builder().name("step1").description("description1").order(1).build());
        stepDtos.add(StepRequestDto.builder().name("step2").description("description2").order(2).build());
        stepDtos.add(StepRequestDto.builder().name("step3").description("description3").order(3).build());
        stepDtos.add(StepRequestDto.builder().name("step4").description("description4").order(4).build());
        stepDtos.add(StepRequestDto.builder().name("step5").description("description5").order(5).build());
        CreateChecklistRequest createChecklistRequest = new CreateChecklistRequest("test title", "test brief", "main1", "test1",
                true, stepDtos, null, null, "colorCode");

        //when
        Long checklistId = checklistService.createChecklist(createChecklistRequest, member);

        //then
        Checklist checklist = checklistService.findById(checklistId);

        //baseInfo
        assertThat(checklist.getOrigin()).isEqualTo(null);
        assertThat(checklist.getOwner()).isEqualTo(member);
        assertThat(checklist.getTitle()).isEqualTo("test title");
        assertThat(checklist.getBrief()).isEqualTo("test brief");
        assertThat(checklist.getCategory().getMainCategory().getName()).isEqualTo("main1");
        assertThat(checklist.getCategory().getName()).isEqualTo("test1");
        assertThat(checklist.getPublish()).isEqualTo(true);
        assertThat(checklist.getSteps().size()).isEqualTo(5);
        assertThat(checklist.getIsCopied()).isEqualTo(false);
        assertThat(checklist.getType()).isEqualTo(ChecklistType.NORMAL);

        //statsInfo
        ChecklistStats statsInfo = checklist.getStatsInfo();
        assertThat(statsInfo.getViewCount()).isEqualTo(0);
        assertThat(statsInfo.getScrapCount()).isEqualTo(0);
        assertThat(statsInfo.getViewCountMale()).isEqualTo(0);
        assertThat(statsInfo.getViewCountFemale()).isEqualTo(0);
        assertThat(statsInfo.getAvgAge()).isEqualTo(0f);

        //usageInfo
        ChecklistUsage usageInfo = checklist.getUsageInfo();
        assertThat(usageInfo.getComplete()).isEqualTo(false);
        assertThat(usageInfo.getProgress()).isEqualTo(0);
        assertThat(usageInfo.getCheckedDate() == null).isEqualTo(true);


        //image
        assertThat(checklist.getImage()).isEqualTo(null);
        assertThat(checklist.getDefaultImage()).isEqualTo(null);
        assertThat(checklist.getColorCode()).isEqualTo("colorCode");
    }

    @Test
    @Transactional
    public void 공개_체크리스트_검색쿼리_조건없음() throws Exception {
        //given
        testChecklistUtils.initChecklist(member);

        //when
        List<Checklist> checklists = checklistService.queryPublishedChecklist(null, null, null, null);
        checklists = checklists.stream().filter(chk -> chk.getTitle().contains("testTitle")).collect(Collectors.toList());

        //then
        assertThat(checklists.size()).isEqualTo(12);
    }

    @Test
    @Transactional
    public void 공개_체크리스트_검색쿼리_제목() throws Exception {
        //given
        testChecklistUtils.initChecklist(member);

        //when
        String title = "testTitle";
        List<Checklist> checklists = checklistService.queryPublishedChecklist(null, null, title, null);

        //then
        assertThat(checklists.size()).isEqualTo(12);
        checklists.stream().forEach(chk -> assertThat(chk.getTitle()).contains(title));
    }

    @Test
    @Transactional
    public void 공개_체크리스트_검색쿼리_조건_카테고리() throws Exception {
        //given
        testChecklistUtils.initChecklist(member);
        String mainCategoryName = "main1";
        String subCategoryName = "test1";

        //when
        List<Checklist> checklists = checklistService.queryPublishedChecklist(mainCategoryName, subCategoryName, null, null);
        checklists = checklists.stream().filter(chk -> chk.getTitle().contains("testTitle")).collect(Collectors.toList());

        //then
        assertThat(checklists.size()).isEqualTo(2);
        checklists.stream().forEach(chk -> assertThat(chk.getCategory().getName()).isEqualTo(subCategoryName));
    }

    @Test
    @Transactional
    public void 공개_체크리스트_검색쿼리_조건_메인카테고리() throws Exception {
        //given
        testChecklistUtils.initChecklist(member);
        String mainCategoryName = "main1";

        //when
        List<Checklist> checklists = checklistService.queryPublishedChecklist(mainCategoryName, null, null, null);
        checklists = checklists.stream().filter(chk -> chk.getTitle().contains("testTitle")).collect(Collectors.toList());

        //then
        assertThat(checklists.size()).isEqualTo(4);
        checklists.stream().forEach(chk -> assertThat(chk.getCategory().getMainCategory().getName()).isEqualTo(mainCategoryName));
    }

    @Test
    @Transactional
    public void 공개_체크리스트_검색쿼리_조건_서브카테고리() throws Exception {
        //given
        testChecklistUtils.initChecklist(member);
        String subCategoryName = "test1";

        //when
        List<Checklist> checklists = checklistService.queryPublishedChecklist(null, subCategoryName, null, null);
        checklists = checklists.stream().filter(chk -> chk.getTitle().contains("testTitle")).collect(Collectors.toList());

        //then
        assertThat(checklists.size()).isEqualTo(6);
        checklists.stream().forEach(chk -> assertThat(chk.getCategory().getName()).isEqualTo(subCategoryName));
    }

    @Test
    @Transactional
    public void 공개_체크리스트_검색쿼리_작성자() throws Exception {
        //given
        testChecklistUtils.initChecklist(member);
        String writer = "testName";

        //when
        List<Checklist> checklists = checklistService.queryPublishedChecklist(null, null, null, writer);
        checklists = checklists.stream().filter(chk -> chk.getTitle().contains("testTitle")).collect(Collectors.toList());

        //then
        assertThat(checklists.size()).isEqualTo(12);
        checklists.stream().forEach(chk -> assertThat(chk.getOwner().getName()).isEqualTo(writer));
    }

    @Test
    @Transactional
    public void 공개_체크리스트_검색쿼리_모두() throws Exception {
        //given
        testChecklistUtils.initChecklist(member);
        String mainCategoryName = "main1";
        String subCategoryName = "test1";
        String title = "testTitle";
        String writer = "testName";

        //when
        List<Checklist> checklists = checklistService.queryPublishedChecklist(mainCategoryName, subCategoryName, title, writer);

        //then
        assertThat(checklists.size()).isEqualTo(2);
        checklists.stream().forEach(chk -> {
            assertThat(chk.getCategory().getMainCategory().getName()).isEqualTo(mainCategoryName);
            assertThat(chk.getCategory().getName()).isEqualTo(subCategoryName);
            assertThat(chk.getTitle()).contains(title);
            assertThat(chk.getOwner().getName()).isEqualTo(writer);
        });
    }

    @Test
    @Transactional
    public void 회원_체크리스트_검색쿼리_조건없음() throws Exception {
        //given
        testChecklistUtils.initChecklist(member);
        testChecklistUtils.createChecklist(member, "testTitle", "testContent", "main1", "test1", true, "colorCode");

        //when
        List<Checklist> checklists = checklistService.queryMemberChecklists(member, null, null, null, null, null, null);

        //then
        assertThat(checklists.size()).isEqualTo(13);
        checklists.stream().forEach(chk -> assertThat(chk.getOwner()).isEqualTo(member));
    }



}