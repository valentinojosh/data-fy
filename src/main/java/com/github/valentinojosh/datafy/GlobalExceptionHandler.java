package com.github.valentinojosh.datafy;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public void defaultErrorHandler(Exception e, HttpServletResponse response) throws IOException {
        String errorMessage = e.getMessage();
        response.sendRedirect("http://localhost:3000/error?message=" + errorMessage);
    }
}