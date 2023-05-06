package com.company.semocheck.common;

import com.company.semocheck.domain.request.category.CreateCategoryRequest;
import com.company.semocheck.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestCategoryUtils {

    @Autowired private CategoryService categoryService;

    public void initCategory(){
        categoryService.createMainCategory("main1");
        categoryService.createMainCategory("main2");
        categoryService.createMainCategory("main3");
        CreateCategoryRequest createForm1 = new CreateCategoryRequest("main1", "test1");
        CreateCategoryRequest createForm2 = new CreateCategoryRequest("main1", "test2");
        CreateCategoryRequest createForm3 = new CreateCategoryRequest("main2", "test1");
        CreateCategoryRequest createForm4 = new CreateCategoryRequest("main2", "test2");
        CreateCategoryRequest createForm5 = new CreateCategoryRequest("main3", "test1");
        CreateCategoryRequest createForm6 = new CreateCategoryRequest("main3", "test2");

        List<CreateCategoryRequest> createFormList = new ArrayList<>();
        createFormList.add(createForm1);
        createFormList.add(createForm2);
        createFormList.add(createForm3);
        createFormList.add(createForm4);
        createFormList.add(createForm5);
        createFormList.add(createForm6);

        for(CreateCategoryRequest request : createFormList){
            categoryService.createSubCategory(request);
        }
    }
}
