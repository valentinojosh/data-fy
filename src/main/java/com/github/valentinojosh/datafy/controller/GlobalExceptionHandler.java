package com.github.valentinojosh.datafy.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public void defaultErrorHandler(Exception e, HttpServletResponse response) throws IOException {
        String errorMessage = e.getMessage();
        response.sendRedirect("https://data-fy.netlify.app/error?message=" + errorMessage);
    }

    @RequestMapping("/custom-error")
    public void handleError(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://data-fy.netlify.app/error?message=Generic+Error");
    }

    @RequestMapping("/**")
    public void handleAllOtherRequests(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://data-fy.netlify.app/error?message=Unknown+Route");
    }
}
