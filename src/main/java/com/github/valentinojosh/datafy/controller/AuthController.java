package com.github.valentinojosh.datafy.controller;
import com.github.valentinojosh.datafy.config.SecretsManager;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

@RestController
@RequestMapping("/api")
@CrossOrigin()
public class AuthController {
    private String clientId;
    private String clientSecret;
    private String uri;
    private String redirect;
    private URI redirectUri;
    private SpotifyApi spotifyApi;

    @Autowired
    public AuthController(SecretsManager secretsManager) {
        // Load secrets into instance variables
        clientId = secretsManager.fetchSecret("CLIENT_ID");
        clientSecret = secretsManager.fetchSecret("CLIENT_SECRET");
        uri = secretsManager.fetchSecret("URI");
        redirect = secretsManager.fetchSecret("REDIRECT");
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



    @GetMapping("/login")
    @ResponseBody
    public String userLogin(HttpSession session){
        if (Boolean.TRUE.equals(session.getAttribute("isAuth"))){
            return "/dash";
        }
        else{
            AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                    .scope("user-top-read, user-library-read, user-read-recently-played")
                    .show_dialog(true)
                    .build();
            final URI userUri = authorizationCodeUriRequest.execute();
            return userUri.toString();
        }
    }

    @GetMapping("/auth")
    public void handleAuthCode(@RequestParam(value = "code", required = false) String userCode, @RequestParam(value = "error", required = false) String error, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException {

        if (error != null) {
            if (error.equals("access_denied")) {
                response.sendRedirect(redirect + "/error?message=" + error);
                return;
            }
        }

        if (userCode == null || userCode.isEmpty()){
            triggerLogout(session);
            response.sendRedirect(redirect);
            return;
        }

        session.setAttribute("isAuth", true);
        getTokens(userCode , session);
        response.sendRedirect(redirect+"/dash");
    }

    private void getTokens(String code, HttpSession session) {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();

        try{
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            //Set access and refresh tokens for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            session.setAttribute("accessToken", spotifyApi.getAccessToken());
            session.setAttribute("refreshToken", spotifyApi.getRefreshToken());

            System.out.println("Expires:" + authorizationCodeCredentials.getExpiresIn());
        }catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    @CrossOrigin(origins = "https://data-fy.netlify.app", allowCredentials = "true")
    @GetMapping("/dash")
    public ResponseEntity<Object> handleDash(HttpSession session) throws IOException {
        if (Boolean.TRUE.equals(session.getAttribute("isAuth"))) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @CrossOrigin(origins = "https://data-fy.netlify.app", allowCredentials = "true")
    @GetMapping("/logout")
    public String userLogout(HttpSession session){
        triggerLogout(session);
        return "/";
    }

    private void triggerLogout(HttpSession session) {
        session.invalidate();
    }

}