package com.company.semocheck.controller.page;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.controller.page.forms.CreateChecklistForm;
import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.FileDetail;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.SubCategory;
import com.company.semocheck.domain.request.checklist.CreateChecklistRequestDto;
import com.company.semocheck.domain.request.checklist.StepRequestDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.CategoryService;
import com.company.semocheck.service.ChecklistService;
import com.company.semocheck.service.FileService;
import com.company.semocheck.service.MemberService;
import com.company.semocheck.utils.MultipartUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChecklistPageController {

    private final ChecklistService checklistService;
    private final CategoryService categoryService;
    private final MemberService memberService;

    private final FileService fileService;

    @GetMapping("/checklists/new")
    public String createForm(Model model){
        List<SubCategory> categories = categoryService.getAllSubCategories();

        CreateChecklistForm form = new CreateChecklistForm();
        for(int i = 0; i < 20; i++){
            StepRequestDto stepDto = new StepRequestDto();
            stepDto.setOrder(i+1);
            form.getSteps().add(stepDto);
        }
        model.addAttribute("form", form);
        model.addAttribute("categories", categories);
        return "checklists/createChecklistForm";
    }

    @PostMapping("/checklists/new")
    public String create(@ModelAttribute CreateChecklistForm form, @RequestParam("image") MultipartFile image){
        Member admin = memberService.findById(1l);
        SubCategory subCategory = categoryService.findSubCategoryById(Long.parseLong(form.getSubCategoryId()));

        //delete not used steps
        List<StepRequestDto> steps = new ArrayList<>();
        int order = 1;
        for (StepRequestDto step : form.getSteps()) {
            if(step.getName().equals("")) break;
            step.setOrder(order); order ++;
            steps.add(step);
        }
        form.setSteps(steps);


        //check validation
        if (image == null || image.isEmpty()) throw new GeneralException(Code.BAD_REQUEST, "파일이 없습니다.");

        //file upload
        String location = String.format("%s/files", "checklists");
        FileDetail fileDetail = fileService.upload(location, image);

        //Create a checklist request
        CreateChecklistRequestDto requestDto = CreateChecklistRequestDto.builder()
                .title(form.getTitle())
                .brief(form.getBrief())
                .publish(form.getPublish())
                .fileId(fileDetail.getId())
                .defaultImage(1)
                .mainCategoryName(subCategory.getMainCategory().getName())
                .subCategoryName(subCategory.getName())
                .steps(form.getSteps())
                .build();

        //Create a checklist Entity
        checklistService.createChecklist(requestDto, admin);

        return "redirect:/checklists";
    }

    @GetMapping("/checklists")
    public String checklists(Model model){
        List<Checklist> checklists = checklistService.getAllChecklist();
        model.addAttribute("checklists", checklists);
        return "checklists/list";
    }

    @GetMapping("/checklists/{checklist_id}")
    public String detail(@PathVariable("checklist_id") Long checklistId, Model model){
        Checklist checklist = checklistService.findById(checklistId);
        model.addAttribute("checklist", checklist);
        return "checklists/detail";
    }

    @PostMapping("/checklists/{checklist_id}/delete")
    public String delete(@PathVariable("checklist_id") Long checklistId){
        Member admin = memberService.findById(1l);
        Checklist checklist = checklistService.findById(checklistId);
        checklistService.adminDeleteChecklist(checklist, admin);
        return "redirect:/checklists";
    }

}
