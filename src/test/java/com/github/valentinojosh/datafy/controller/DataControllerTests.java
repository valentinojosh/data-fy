package com.github.valentinojosh.datafy.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataControllerTests {
    @Test
    void extractsStandardAndJsonEncodedBearerTokens() {
        assertEquals("access-token", DataController.extractBearerToken("Bearer access-token").orElseThrow());
        assertEquals("access-token", DataController.extractBearerToken("Bearer \"access-token\"").orElseThrow());
    }

    @Test
    void rejectsMissingOrMalformedBearerTokens() {
        assertTrue(DataController.extractBearerToken(null).isEmpty());
        assertTrue(DataController.extractBearerToken("Basic credentials").isEmpty());
        assertTrue(DataController.extractBearerToken("Bearer   ").isEmpty());
    }
}
