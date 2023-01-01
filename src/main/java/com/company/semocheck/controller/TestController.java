package com.company.semocheck.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/code/kakao")
    public ResponseEntity<String> codeKakao(@RequestParam("code") String code, @RequestParam("state") String state){
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 만들기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HTTP Body로 들어갈 것들 만들기
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "617e6fd9bd129dd5c62945bf7fd8ea95");
        params.add("redirect_uri", "http://localhost:8080/api/test/code/kakao");
        params.add("code", code);
        params.add("state", state);

        // Set http entity -> Body 데이터와 헤더 묶기
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://kauth.kakao.com/oauth/token", request, String.class);

        return response;
    }

    @GetMapping("/code/google")
    public ResponseEntity<String> codeGoogle(@RequestParam("code") String code, @RequestParam("state") String state){
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 만들기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HTTP Body로 들어갈 것들 만들기
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", "983039958847-rma5uoq5atl44ch82t9a0k3cga7vs6p2.apps.googleusercontent.com");
        params.add("client_secret", "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");
        params.add("redirect_uri", "http://localhost:8080/api/test/code/google");
        params.add("grant_type", "authorization_code");
        params.add("state", state);

        // Set http entity -> Body 데이터와 헤더 묶기
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://oauth2.googleapis.com/token", request, String.class);

        return response;
    }

}
