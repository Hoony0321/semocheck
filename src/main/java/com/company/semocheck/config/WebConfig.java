package com.company.semocheck.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {

    public static final String ALLOWED_METHOD_NAMES = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") //TODO( 프론트 origin 주소만 열어두기 )
                .exposedHeaders("authorization")
                .allowedMethods(ALLOWED_METHOD_NAMES.split(","))
                .maxAge(3000);
    }
}
