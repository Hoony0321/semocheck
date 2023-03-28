package com.company.semocheck.controller;

import com.company.semocheck.service.ChecklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChecklistControllerTest {

    @Autowired private ChecklistController checklistController;
    @Autowired private ChecklistService checklistService;



}