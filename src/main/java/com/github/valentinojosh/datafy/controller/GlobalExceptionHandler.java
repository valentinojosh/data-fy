package com.github.valentinojosh.datafy.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Controller
@ControllerAdvice
public class GlobalExceptionHandler {
    private final String frontendOrigin;

    public GlobalExceptionHandler(@Value("${frontend.origin}") String frontendOrigin) {
        this.frontendOrigin = frontendOrigin;
    }

    @ExceptionHandler(value = Exception.class)
    public void defaultErrorHandler(Exception e, HttpServletResponse response) throws IOException {
        response.sendRedirect(errorUrl(e.getMessage()));
    }

    @RequestMapping("/custom-error")
    public void handleError(HttpServletResponse response) throws IOException {
        response.sendRedirect(errorUrl("Generic Error"));
    }

    @RequestMapping("/**")
    public void handleAllOtherRequests(HttpServletResponse response) throws IOException {
        response.sendRedirect(errorUrl("Unknown Route"));
    }

    private String errorUrl(String message) {
        String safeMessage = message == null || message.isBlank() ? "Unexpected error" : message;
        return UriComponentsBuilder.fromUriString(frontendOrigin)
                .path("/error")
                .queryParam("message", safeMessage)
                .build()
                .encode()
                .toUriString();
    }
}
