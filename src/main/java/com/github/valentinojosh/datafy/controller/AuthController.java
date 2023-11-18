package com.github.valentinojosh.datafy.controller;
import com.github.valentinojosh.datafy.config.SecretsManager;
import jakarta.servlet.http.HttpSession;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://data-fy.netlify.app/")
public class AuthController {
    private final URI redirectUri;
    private final SpotifyApi spotifyApi;

    @Autowired
    public AuthController(SecretsManager secretsManager) {
        // Load secrets into instance variables
        String clientId = secretsManager.fetchSecret("CLIENT_ID");
        String clientSecret = secretsManager.fetchSecret("CLIENT_SECRET");
        String uri = secretsManager.fetchSecret("URI");
        redirectUri = SpotifyHttpManager.makeUri(uri);

        spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();
    }

    public URI getRedirectUri() {
        return redirectUri;
    }

    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }

    @PostMapping("/token")
    public ResponseEntity<?> handleAuthCode(@RequestBody Map<String, String> payload) {
        // Exchange code for tokens
        String code = payload.get("code");
        getTokens(code);
        String token = spotifyApi.getAccessToken();

        return ResponseEntity.ok(token);
    }

    @GetMapping("/login")
    @ResponseBody
    public String userLogin(HttpSession session){
            AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                    .scope("user-top-read, user-library-read, user-read-recently-played")
                    .show_dialog(true)
                    .build();
            final URI userUri = authorizationCodeUriRequest.execute();
            return userUri.toString();
    }

    private void getTokens(String code) {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();

        try{
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            //Set access and refresh tokens for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires:" + authorizationCodeCredentials.getExpiresIn());
        }catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}