package com.github.valentinojosh.datafy.controller;
import com.github.valentinojosh.datafy.config.SecretsManager;
import jakarta.servlet.http.HttpSession;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
//https://data-fy.netlify.app/
//http://localhost:3000/

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000/")
public class AuthController {
    private final SecretsManager secretsManager;

    @Autowired
    public AuthController(SecretsManager secretsManager) {
        this.secretsManager = secretsManager;
    }

    private SpotifyApi createSpotifyApi() {
        String clientSecret = secretsManager.fetchSecret("CLIENT_SECRET");
        String clientId = "1b24d7a17fea44f59005605f6cb96cf2";
        URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:3000/auth-callback");

        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();
    }

    @PostMapping("/token")
    public ResponseEntity<?> handleAuthCode(@RequestBody Map<String, String> payload) {
        // Exchange code for tokens
        SpotifyApi spotifyApi = createSpotifyApi();
        String code = payload.get("code");
        String token = getTokens(code, spotifyApi);

        if (token == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error: Failed to exchange code for token");
        }

        return ResponseEntity.ok(token);
    }

    @GetMapping("/login")
    @ResponseBody
    public String userLogin(HttpSession session){
        SpotifyApi spotifyApi = createSpotifyApi();
            AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                    .scope("user-top-read, user-library-read, user-read-recently-played")
                    .show_dialog(true)
                    .build();
            final URI userUri = authorizationCodeUriRequest.execute();
            return userUri.toString();
    }

    private String getTokens(String code, SpotifyApi spotifyApi) {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();
        String token = null;
        try{
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            token = authorizationCodeCredentials.getAccessToken();
        }catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return token;
    }

}