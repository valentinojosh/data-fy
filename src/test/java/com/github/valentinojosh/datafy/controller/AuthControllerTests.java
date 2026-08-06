package com.github.valentinojosh.datafy.controller;

import com.github.valentinojosh.datafy.config.SpotifyApiFactory;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class AuthControllerTests {
    private final SpotifyApiFactory spotifyApiFactory = mock(SpotifyApiFactory.class);
    private final AuthController controller = new AuthController(spotifyApiFactory);

    @Test
    void rejectsMissingAuthorizationCodeBeforeCallingSpotify() {
        ResponseEntity<?> response = controller.handleAuthCode(Collections.emptyMap());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(spotifyApiFactory);
    }

    @Test
    void rejectsBlankAuthorizationCodeBeforeCallingSpotify() {
        ResponseEntity<?> response = controller.handleAuthCode(Map.of("code", "  "));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(spotifyApiFactory);
    }
}
