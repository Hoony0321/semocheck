package com.company.semocheck.common;

import com.company.semocheck.domain.request.category.CreateCategoryRequest;
import com.company.semocheck.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        CreateCategoryRequest createForm3 = new CreateCategoryRequest("main2", "test3");
        CreateCategoryRequest createForm4 = new CreateCategoryRequest("main2", "test4");
        CreateCategoryRequest createForm5 = new CreateCategoryRequest("main3", "test5");

        List<CreateCategoryRequest> createFormList = new ArrayList<>();
        createFormList.add(createForm1);
        createFormList.add(createForm2);
        createFormList.add(createForm3);
        createFormList.add(createForm4);
        createFormList.add(createForm5);

        for(CreateCategoryRequest request : createFormList){
            categoryService.createSubCategory(request);
        }
    }
}
