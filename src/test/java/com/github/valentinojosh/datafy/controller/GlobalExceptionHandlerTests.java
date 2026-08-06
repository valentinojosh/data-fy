package com.github.valentinojosh.datafy.controller;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTests {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler("http://localhost:3000");

    @Test
    void redirectsExceptionsToConfiguredFrontendWithEncodedMessage() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.defaultErrorHandler(new IllegalStateException("bad value & next"), response);

        assertEquals("http://localhost:3000/error?message=bad%20value%20%26%20next",
                response.getRedirectedUrl());
    }
}
