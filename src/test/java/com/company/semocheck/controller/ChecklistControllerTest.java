package com.company.semocheck.controller;

import com.company.semocheck.domain.dto.SearchResultDto;
import com.company.semocheck.domain.dto.checklist.ChecklistSimpleDto;
import com.company.semocheck.service.ChecklistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChecklistControllerTest {

    @Autowired private ChecklistController checklistController;
    @Autowired private ChecklistService checklistService;



}