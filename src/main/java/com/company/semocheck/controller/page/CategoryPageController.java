package com.company.semocheck.controller.page;

import com.company.semocheck.controller.CategoryController;
import com.company.semocheck.controller.page.forms.CreateCategoryForm;
import com.company.semocheck.domain.FileDetail;
import com.company.semocheck.domain.category.MainCategory;
import com.company.semocheck.domain.category.SubCategory;
import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.dto.FileDto;
import com.company.semocheck.domain.request.category.CreateCategoryRequest;
import com.company.semocheck.service.CategoryService;
import com.company.semocheck.service.FileService;
import com.company.semocheck.service.checklist.ChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryPageController {

    private final CategoryService categoryService;
    private final CategoryController categoryController;
    private final ChecklistService checklistService;
    private final FileService fileService;

    @GetMapping("")
    public String categories(Model model){
        List<MainCategory> mainCategories = categoryService.getAllMainCategories();
        List<SubCategory> subCategories = categoryService.getAllSubCategories();
        model.addAttribute("mainCategories", mainCategories);

        Collections.sort(subCategories, Comparator.comparing(c -> c.getMainCategory().getId()));
        model.addAttribute("subCategories", subCategories);
        return "categories/list";
    }

    @GetMapping("/new")
    public String createForm(Model model){
        List<MainCategory> mainCategories = categoryService.getAllMainCategories();
        List<SubCategory> subCategories = categoryService.getAllSubCategories();

        CreateCategoryForm form = new CreateCategoryForm();
        model.addAttribute("form", form);
        model.addAttribute("mainCategories", mainCategories);
        Collections.sort(subCategories, Comparator.comparing(c -> c.getMainCategory().getId()));
        model.addAttribute("subCategories", subCategories);
        return "categories/create";
    }

    @PostMapping("/new")
    public String createForm(@ModelAttribute CreateCategoryForm form){
        if(form.getSubName().equals("")){//1차 카테고리 생성
            categoryService.createMainCategory(form.getMainName());
        }
        else{//2차 카테고리 생성
            categoryService.createSubCategory(new CreateCategoryRequest(form.getMainName(), form.getSubName()));
        }
        return "redirect:/categories/new";
    }

    @GetMapping("/{id}/image")
    public String categoryImages(Model model, @PathVariable("id") Long id){
        SubCategory subCategory = categoryService.findSubCategoryById(id);
        String folderName = "categories/" +  subCategory.getMainCategory().getName() + "/" + subCategory.getName();
        List<FileDetail> fileDetails = fileService.findByFolder(folderName);

        List<FileDto> images = FileDto.createDtos(fileDetails);

        model.addAttribute("images", images);
        model.addAttribute("subCategory", subCategory);
        return "categories/image";
    }

    @PostMapping("/{id}/image/new")
    public String createImageForm(@RequestParam("imageFile") MultipartFile file, @PathVariable("id") Long id){
        SubCategory subCategory = categoryService.findSubCategoryById(id);
        categoryController.uploadDefaultImageFile(file, subCategory.getMainCategory().getName(), subCategory.getName());
        return "redirect:/categories/" + id + "/image";
    }

    @Transactional
    @GetMapping("/{category_id}/image/{image_id}/delete")
    public String deleteImage(@PathVariable("category_id") Long categoryId, @PathVariable("image_id") String imageId){
        FileDetail fileDetail = fileService.findById(imageId);
        List<Checklist> allChecklists = checklistService.findAllChecklists();
        for (Checklist checklist : allChecklists) {
            if(checklist.getImage() != null && checklist.getImage().equals(fileDetail)){
                checklist.setImage(null);
            }
            else if(checklist.getDefaultImage() != null && checklist.getDefaultImage().equals(fileDetail)){
                checklist.setDefaultImage(null);
            }
        }
        fileService.removeFile(fileDetail);

        return "redirect:/categories/" + categoryId + "/image";
    }
}
