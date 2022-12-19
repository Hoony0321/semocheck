package com.company.semocheck.config;

import com.company.semocheck.auth.jwt.*;
import com.company.semocheck.auth.oauth2.CustomOAuth2UserService;
import com.company.semocheck.auth.oauth2.OAuth2FailureHandler;
import com.company.semocheck.auth.oauth2.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtProvider jwtProvider;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // CSRF 설정 disable
        http.csrf().disable()

                // exception handling 할 때 만든 에러 헨들링 클래스 추가
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // 시큐리티는 기본적으로 세션을 사용
                // 하지만 JWT 세션을 사용하지 않기때문에 stateless로 설정
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // h2-console을 위한 설정 추가
                .and()
                    .headers()
                    .frameOptions()
                    .disable()

                // swagger url 모음
                .and()
                    .authorizeHttpRequests()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/v3/api-docs/**").permitAll()

                // 허용 url 모음
                .and()
                    .authorizeHttpRequests()
                    .requestMatchers("/api/auth/refresh").permitAll()
                    .requestMatchers("/api/auth/login").permitAll()
                    .requestMatchers("/api/test/**").permitAll()
                    .requestMatchers(HttpMethod.GET,"/api/auth/token").permitAll()
                    .requestMatchers(HttpMethod.GET,"/api/auth/token/fail").permitAll()
                    .requestMatchers("/logout").permitAll()

                // ROLE url 모음
                .and()
                    .authorizeHttpRequests()
                    .requestMatchers("/api/guest").hasRole("GUEST")
                    .requestMatchers("/api/admin").hasRole("ADMIN")
                    .anyRequest().authenticated()

                .and()
                    .logout()
                    .logoutSuccessUrl("/")

                .and()
                    .oauth2Login()
                    .authorizationEndpoint()
                    .baseUri("/oauth2/authorization")
                    .and()
                    .redirectionEndpoint()
                    .baseUri("/*/oauth2/code/*")
                    .and()
                    .successHandler(oAuth2SuccessHandler)
                    .failureHandler(oAuth2FailureHandler)
                    .userInfoEndpoint()
                    .userService(customOAuth2UserService)

                .and()
                .and()
                .apply(new JwtSecurityConfig(jwtProvider));


        return http.build();

    }
}
