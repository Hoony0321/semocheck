package com.company.semocheck.controller.page;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.DataResponseDto;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.controller.CategoryController;
import com.company.semocheck.controller.page.forms.CreateChecklistForm;
import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.FileDetail;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.category.SubCategory;
import com.company.semocheck.domain.dto.FileDto;
import com.company.semocheck.domain.request.checklist.CreateChecklistRequest;
import com.company.semocheck.domain.request.checklist.StepRequestDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.service.CategoryService;
import com.company.semocheck.service.checklist.ChecklistService;
import com.company.semocheck.service.FileService;
import com.company.semocheck.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ChecklistPageController {

    private final ChecklistService checklistService;
    private final CategoryService categoryService;
    private final MemberService memberService;
    private final FileService fileService;

    private final CategoryController categoryController;

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
        Member admin = memberService.findById(1l); //TODO : 추후에 HttpRequest에서 admin member 정보로 대체
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

        //Create a checklist request
        CreateChecklistRequest requestDto = CreateChecklistRequest.builder()
                .title(form.getTitle())
                .brief(form.getBrief())
                .publish(form.getPublish())
                .mainCategoryName(subCategory.getMainCategory().getName())
                .subCategoryName(subCategory.getName())
                .steps(form.getSteps())
                .build();

        // image setting
        if (image.isEmpty() || image == null){ // use default category image
            DataResponseDto<FileDto> response = categoryController.getDefaultImage(subCategory.getMainCategory().getName(), subCategory.getName());
            FileDto fileDto = response.getData();
            requestDto.setDefaultImageId(fileDto.getId());
        }
        else{ //file upload
            String location = String.format("%s/files", "checklists");
            FileDetail fileDetail = fileService.upload(location, image);
            requestDto.setImageId(fileDetail.getId());
        }

        //Create a checklist Entity
        checklistService.createChecklist(requestDto, admin);

        return "redirect:/checklists";
    }

    @GetMapping("/checklists")
    public String checklists(@RequestParam(value = "category", required = false) Long categoryId, Model model){
        List<Checklist> checklists = checklistService.findAllChecklists();
        if(categoryId != null) {
            checklists = checklists.stream().filter(chk -> chk.getCategory().getId().equals(categoryId)).collect(Collectors.toList());
        }

        List<SubCategory> categories = categoryService.getAllSubCategories();
        Collections.reverse(checklists);
        model.addAttribute("selected", categoryId);
        model.addAttribute("checklists", checklists);
        model.addAttribute("categories", categories);
        return "checklists/list";
    }

    @GetMapping("/checklists/{checklist_id}")
    public String detail(@PathVariable("checklist_id") Long checklistId, Model model){
        Checklist checklist = checklistService.findById(checklistId);
        FileDto fileDto = FileDto.createDto(checklist.getImage());

        model.addAttribute("checklist", checklist);

        if(fileDto != null){
            model.addAttribute("imageUrl", fileDto.getPath());
        }
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
