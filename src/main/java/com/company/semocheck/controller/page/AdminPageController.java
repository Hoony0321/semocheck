package com.company.semocheck.controller.page;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Slf4j
@Controller
//@RequestMapping("/admin") TODO : 추후에 admin 인증 로직 완성되면 주석풀기
public class AdminPageController {
    @GetMapping("/")
    public String home(HttpServletRequest request){
        return "home";
    }
}
