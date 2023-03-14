package com.company.semocheck.controller.page;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class HomePageController {
    @GetMapping("/")
    public String home(){
        return "home";
    }
}
