package com.github.valentinojosh.datafy.controller;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.hc.core5.http.ParseException;
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

    //Current idea: get code -> directly fire off access token query, -> directly get 5+ queries for data wanted
    //Then store that data in https session. This way the front end can grab it and display it, and aftet 30-60 min
    //the data would expire with the session

    //Each user should get their own instance of the controller
    //For sensitive data I could use encrypted cookies or secure database.
    // Database would be something I am more familiar with, plus its less web dev-y
    //This project is not meant to show suberb web dev skills, it is to show general ability in Java, React, Frameworks, REST, etc.

    private static final String clientId = Dotenv.load().get("CLIENT_ID");
    private static final String clientSecret = Dotenv.load().get("CLIENT_SECRET");
    private static final String uri = Dotenv.load().get("URI");
    private static final URI redirectUri = SpotifyHttpManager.makeUri(uri);

    private String code = null;

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectUri)
            .build();

    @GetMapping("/login")
    @ResponseBody
    public String userLogin(){
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope("user-read-private, user-read-email, user-top-read, user-library-read, user-read-recently-played")
                .show_dialog(true)
                .build();
        final URI uri = authorizationCodeUriRequest.execute();
        return uri.toString();
    }

    @GetMapping("/auth/")
    public void handleAuthCode(@RequestParam(value = "code", required = false) String userCode, @RequestParam(value = "error", required = false) String error, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException {

        if (error != null) {
            if (error.equals("access_denied")) {
                response.sendRedirect("http://localhost:3000/error?message=" + error);
                return;
            }
        }

        if (userCode == null || userCode.isEmpty()){
            return;
        }

        //Can make a future function here for multithread purpose. this way the redirect always happens first
        getTokens(userCode , session);

        code = "0";
        response.sendRedirect("http://localhost:3000/dash");
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

    @GetMapping("/dash")
    public Boolean handleDash() throws IOException {
        //Returns true if code is null, which prevents dash access
        return (code == null);
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @GetMapping("/logout")
    public String userLogout(HttpSession session){
        session.invalidate();
        code = null;
        return "/";
    }

}