package com.company.semocheck.auth.jwt;

import com.company.semocheck.exception.ErrorCode;
import com.company.semocheck.exception.FilterException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        FilterException exception = (FilterException) request.getAttribute("exception");
        setResponse(response, exception);
    }

    //한글 출력을 위해 getWriter() 사용
    private void setResponse(HttpServletResponse response, FilterException exception) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        response.setStatus(exception.getStatus().value());

        JSONObject responseJson = new JSONObject();
        responseJson.put("code", exception.getStatus().name());
        responseJson.put("message", exception.getMessage());


        response.getWriter().print(responseJson);
    }
}
