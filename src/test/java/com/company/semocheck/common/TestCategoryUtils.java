package com.company.semocheck.common;

import com.company.semocheck.domain.request.category.CreateCategoryRequest;
import com.company.semocheck.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestCategoryUtils {

    @Autowired
    private CategoryService categoryService;

    @Transactional
    public void initCategory(){
        CreateCategoryRequest createForm1 = new CreateCategoryRequest("생활", "결혼");
        CreateCategoryRequest createForm2 = new CreateCategoryRequest("생활", "부동산");
        CreateCategoryRequest createForm3 = new CreateCategoryRequest("생활", "인테리어");
        CreateCategoryRequest createForm4 = new CreateCategoryRequest("커리어", "면접");
        CreateCategoryRequest createForm5 = new CreateCategoryRequest("커리어", "이직");

        List<CreateCategoryRequest> createFormList = new ArrayList<>();
        createFormList.add(createForm1);
        createFormList.add(createForm2);
        createFormList.add(createForm3);
        createFormList.add(createForm4);
        createFormList.add(createForm5);

        for(CreateCategoryRequest request : createFormList){
            categoryService.createCategory(request);
        }
    }
}
