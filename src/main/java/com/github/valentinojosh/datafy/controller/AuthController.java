package com.github.valentinojosh.datafy.controller;

import com.github.valentinojosh.datafy.config.SpotifyApiFactory;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${cors.origin}")
public class AuthController {
    private final SpotifyApiFactory spotifyApiFactory;

    @Autowired
    public AuthController(SpotifyApiFactory spotifyApiFactory) {
        this.spotifyApiFactory = spotifyApiFactory;
    }

    @PostMapping("/token")
    public ResponseEntity<?> handleAuthCode(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body("Error: Authorization code is required");
        }

        SpotifyApi spotifyApi = spotifyApiFactory.create();
        String token = getTokens(code, spotifyApi);

        if (token == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error: Failed to exchange code for token");
        }

        return ResponseEntity.ok(token);
    }

    @GetMapping("/login")
    public String userLogin() {
        SpotifyApi spotifyApi = spotifyApiFactory.create();
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope("user-top-read, user-read-recently-played")
                .show_dialog(true)
                .build();
        final URI userUri = authorizationCodeUriRequest.execute();
        return userUri.toString();
    }

    private String getTokens(String code, SpotifyApi spotifyApi) {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
            return authorizationCodeCredentials.getAccessToken();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}
