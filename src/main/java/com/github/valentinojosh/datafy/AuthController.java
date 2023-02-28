package com.github.valentinojosh.datafy;

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
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.artists.GetSeveralArtistsRequest;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;


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
    private Artist[] artistsShort;
    private Artist[] artistsMedium;
    private Artist[] artistsLong;
    private Artist[] artistsLongTotal;
    private PlayHistory[] recentTrackTotal;
    ArrayList<ArtistSimplified> recentArtists = new ArrayList<>();
    private Artist[] fullArists;
    Map<String, Integer> topSixGenres = new LinkedHashMap<>();

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

//    @RequestMapping("/error")
//    public void userLogin(HttpServletResponse response) throws IOException {
//        response.sendRedirect("http://localhost:3000");
//    }

    @GetMapping("/auth/")
    public void handleAuthCode(@RequestParam("code") String userCode, HttpServletResponse response, HttpServletRequest request) throws IOException {

        if (userCode == null || userCode.isEmpty()){
            // Return a bad request response
            response.sendRedirect("/");
            //response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No code provided in request body");
            return;
        }

        //Can make a future function her efor multithread purpose. this way the redirect always happens first
        getTokens(userCode);
        getTopArtists();
        //getTotalMinutes();
        getTopGenres();

        code = "0";
        response.sendRedirect("http://localhost:3000/dash");
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

    private void getTopArtists() {
        //System.out.println("Here we have made it to the getSpotifyData method");
        final  GetUsersTopArtistsRequest getUsersTopArtistsRequestShort = spotifyApi.getUsersTopArtists()
                .time_range("short_term")
                .limit(5)
                .build();

        final  GetUsersTopArtistsRequest getUsersTopArtistsRequestMedium = spotifyApi.getUsersTopArtists()
                .time_range("medium_term")
                .limit(5)
                .build();

        final  GetUsersTopArtistsRequest getUsersTopArtistsRequestLong = spotifyApi.getUsersTopArtists()
                .time_range("long_term")
                .limit(5)
                .build();

        final  GetUsersTopArtistsRequest getUsersTopArtistsRequestLongTotal = spotifyApi.getUsersTopArtists()
                .time_range("long_term")
                .limit(50)
                .build();

        try{
            artistsShort = getUsersTopArtistsRequestShort.execute().getItems();

            final Paging<Artist> artistPaging = getUsersTopArtistsRequestMedium.execute();
            artistsMedium = artistPaging.getItems();

            artistsLong = getUsersTopArtistsRequestLong.execute().getItems();

            artistsLongTotal = getUsersTopArtistsRequestLongTotal.execute().getItems();
        } catch (Exception e){
            System.out.println("Error getting top artists: " + e.getMessage());
        }
    }

    private void getTopGenres() {
        //Start by grabbing max limit top artists. then populate through hashmap by genre. weigh these more heavily then max limit of recents // +28 each?
        //Next get the max limit of most recent tracks played. add into the same previous hashmap by genre // +4 each?
        //NEED TO: translate recent tracks into artists and their genres
        final  GetCurrentUsersRecentlyPlayedTracksRequest getCurrentUsersRecentlyPlayedTracksRequest = spotifyApi.getCurrentUsersRecentlyPlayedTracks()
                .limit(50)
                .build();

        Map<String, Integer> genres = new HashMap<>();
        try{
            //hash set because it increases the odd of giving a bit of diversity into the genres considering it is only most recent 50 songs
            HashSet<String> artistsIDs = new HashSet<>();
            recentTrackTotal = getCurrentUsersRecentlyPlayedTracksRequest.execute().getItems();

            //ArrayList<ArtistSimplified> recentArtists = new ArrayList<>();

            for (PlayHistory currentTrack : recentTrackTotal) {
                recentArtists.addAll(Arrays.asList(currentTrack.getTrack().getArtists()));
            }

            for(ArtistSimplified currentArtist : recentArtists){
                if(artistsIDs.size() >= 50) {
                    break;
                }
                artistsIDs.add(currentArtist.getId());
            }

            String csvIDs = String.join(",", artistsIDs.toArray(new String[artistsIDs.size()]));

            //Get artist genres here with spotify request

            final GetSeveralArtistsRequest getSeveralArtistsRequest = spotifyApi.getSeveralArtists(csvIDs)
                    .build();

            fullArists = getSeveralArtistsRequest.execute();

            //genres sourced from recent tracks
            for(Artist currentArtist : fullArists){
                String[] genre = currentArtist.getGenres();
                for(String currentGenre : genre){
                    if (genres.containsKey(currentGenre)) {
                        //If present, add 4
                        genres.put(currentGenre, (genres.get(currentGenre) + 4));
                    } else {
                        //If not present, add it with value of 4
                        genres.put(currentGenre, 4);
                    }
                }
            }

            //the genres sourced from the top artists are weighted more heavily
            //im thinking maybe increasing the difference even more considering recent songs will change more than recent plays
            //maybe do a +8 vs +3?
            for(Artist currentArtist : artistsLongTotal){
                String[] genre = currentArtist.getGenres();
                for(String currentGenre : genre){
                    if (genres.containsKey(currentGenre)) {
                        //If present, add 6
                        genres.put(currentGenre, genres.get(currentGenre) + 6);
                    } else {
                        //If not present, add it with value of 6
                        genres.put(currentGenre, 6);
                    }
                }
            }

            HashMap<String, Integer> sortedGenres = genres.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));

            int counter = 0;
            for (Map.Entry<String, Integer> entry : sortedGenres.entrySet()) {
                if (counter >= 6) {
                    break;
                }
                topSixGenres.put(entry.getKey(), entry.getValue());
                counter++;
            }

        } catch (Exception e){
            System.out.println("Error getting top genres: " + e.getMessage());
        }

    }

    private void getTotalMinutes() {
        //posisbly impossible lol. or could do in a jank way that only does the past week or less
        //could do minutes based upon 1 week of listening. for the full year that would be 1 week of recent tracks added up
        //might need to do less than 1 week. maybe based off one day?
    }

    private void getSpotifyDataRec() {
        //to make this work need to pass the 3 required variables from the aritst/song bla bla. could be cool aspect. but would need tracks first
        //aka this is an aspect to finish later
        final GetRecommendationsRequest getRecommendationsRequest = spotifyApi.getRecommendations()
                .build();

        try {
            final Recommendations recommendations = getRecommendationsRequest.execute();

            System.out.println("Length: " + recommendations.getTracks().length);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @GetMapping("/dash")
    public Boolean handleDash() throws IOException {
        //Returns true if code is null, which prevents dash access
        return (code == null);
    }

    @GetMapping("/topfive")
    public Artist[] handleTopFive(HttpServletResponse response, HttpServletRequest request) throws IOException {
        return artistsShort;
    }

    @GetMapping("/genres")
    public Map<String, Integer> handleGenre(HttpServletResponse response, HttpServletRequest request) throws IOException {
        return topSixGenres;
    }

    @GetMapping("/logout")
    @ResponseBody
    public String userLogout(HttpServletRequest request){
        //before i am able to make this work i need to decide what to do with the data stored in here
        //there is the code and artists that should be considered to be stored in a session or elsewhere on the client side?
        //to keep secure i put it in cookies or something?
        HttpSession session = request.getSession();
        session.invalidate();
        return "/";
    }

}