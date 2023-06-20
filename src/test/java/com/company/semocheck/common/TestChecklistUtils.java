package com.company.semocheck.common;

import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.request.checklist.CreateChecklistRequest;
import com.company.semocheck.domain.request.checklist.StepRequestDto;
import com.company.semocheck.form.CreateChecklistForm;
import com.company.semocheck.form.CreateStepForm;
import com.company.semocheck.service.MemberService;
import com.company.semocheck.service.checklist.ChecklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class TestChecklistUtils {

    @Autowired MemberService memberService;
    @Autowired ChecklistService checklistService;
    private List<CreateStepForm> stepDtos = new ArrayList<>();
    Random random = new Random();


    public Long createChecklist(Member member, String title, String brieft, String mainCategory, String subCategory,
                                boolean isPublic, String colorCode){

        stepDtos.add(CreateStepForm.builder().stepName("step1").stepDescription("description1").build());
        stepDtos.add(CreateStepForm.builder().stepName("step2").stepDescription("description2").build());
        stepDtos.add(CreateStepForm.builder().stepName("step3").stepDescription("description3").build());
        stepDtos.add(CreateStepForm.builder().stepName("step4").stepDescription("description4").build());
        stepDtos.add(CreateStepForm.builder().stepName("step5").stepDescription("description5").build());
        CreateChecklistForm form = new CreateChecklistForm(title, brieft, mainCategory, subCategory,
                isPublic, stepDtos, null, null, colorCode);

        Long checklistId = checklistService.createChecklist(form, member);

        return checklistId;
    }

    public void initChecklist(Member member){ //12개 체크리스트 생성 -> 각 main당 4개씩, main에 test1,test2 각 2개씩

        for(int i = 1; i < 4; i++){
            for(int j =1; j < 3; j++){
                String title = "testTitle" + i + j;
                String brief = "brief" + i + j;
                String mainCategory = "main" + i;
                String subCategory = "test" + j;

                this.createChecklist(member, title, brief, mainCategory, subCategory, true, "colorCode");
            }
        }
        for(int i = 1; i < 4; i++){
            for(int j =1; j < 3; j++){
                String title = "testTitle" + i + j;
                String brief = "brief" + i + j;
                String mainCategory = "main" + i;
                String subCategory = "test" + j;

                this.createChecklist(member, title, brief, mainCategory, subCategory, true, "colorCode");
            }
        }
    }
}
