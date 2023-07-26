package com.github.valentinojosh.datafy.controller;

import com.github.valentinojosh.datafy.object.SpotifyData;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpSession;
import org.apache.hc.core5.http.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
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
public class DataController {
    private static final String clientId = Dotenv.load().get("CLIENT_ID");
    private static final String clientSecret = Dotenv.load().get("CLIENT_SECRET");
    private static final String uri = Dotenv.load().get("URI");
    private static final URI redirectUri = SpotifyHttpManager.makeUri(uri);

    private final SpotifyData sd = new SpotifyData();
    //private Track[] tracksShort;
    //private Track[] tracksMedium;
    //private Track[] tracksLong;

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectUri)
            .build();

    @GetMapping("/topfive")
    public Artist[] handleTopFive() throws IOException {
        return sd.getArtistsShort();
    }

    @GetMapping("/genres")
    public Map<String, Integer> handleGenre() throws IOException {
        return sd.getTopSixGenres();
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/data")
    public ResponseEntity<Void> getData(HttpSession session) {
        // Retrieve the access token and refresh token from the session
        String accessToken = (String) session.getAttribute("accessToken");
        String refreshToken = (String) session.getAttribute("refreshToken");
        spotifyApi.setAccessToken(accessToken);
        spotifyApi.setRefreshToken(refreshToken);

        getTopArtists();
        getTotalMinutes();
        getTopGenres();

        return new ResponseEntity<>(HttpStatus.OK);
    }


    private void getTopArtists() {
        //System.out.println("Here we have made it to the getSpotifyData method");
        final GetUsersTopArtistsRequest getUsersTopArtistsRequestShort = spotifyApi.getUsersTopArtists()
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
            sd.setArtistsShort(getUsersTopArtistsRequestShort.execute().getItems());
            sd.setArtistsMedium(getUsersTopArtistsRequestMedium.execute().getItems());
            sd.setArtistsLong(getUsersTopArtistsRequestLong.execute().getItems());
            sd.setArtistsLongTotal(getUsersTopArtistsRequestLongTotal.execute().getItems());
        } catch (Exception e){
            System.out.println("Error getting top artists: " + e.getMessage());
        }
    }

    private void getTopGenres() {
        //Start by grabbing max limit top artists. then populate through hashmap by genre. weigh these more heavily then max limit of recents // +28 each?
        //Next get the max limit of most recent tracks played. add into the same previous hashmap by genre // +4 each?
        //NEED TO: translate recent tracks into artists and their genres
        final GetCurrentUsersRecentlyPlayedTracksRequest getCurrentUsersRecentlyPlayedTracksRequest = spotifyApi.getCurrentUsersRecentlyPlayedTracks()
                .limit(50)
                .build();

        Map<String, Integer> genres = new HashMap<>();
        try{
            //hash set because it increases the odd of giving a bit of diversity into the genres considering it is only most recent 50 songs
            HashSet<String> artistsIDs = new HashSet<>();
            sd.setRecentTrackTotal(getCurrentUsersRecentlyPlayedTracksRequest.execute().getItems());

            ArrayList<ArtistSimplified> temp = new ArrayList<>();

            for (PlayHistory currentTrack : sd.getRecentTrackTotal()) {
                temp.addAll(Arrays.asList(currentTrack.getTrack().getArtists()));
            }

            sd.setRecentArtists(temp);

            for(ArtistSimplified currentArtist : temp){
                if(artistsIDs.size() >= 50) {
                    break;
                }
                artistsIDs.add(currentArtist.getId());
            }

            String csvIDs = String.join(",", artistsIDs.toArray(new String[artistsIDs.size()]));

            //Get artist genres here with spotify request

            final GetSeveralArtistsRequest getSeveralArtistsRequest = spotifyApi.getSeveralArtists(csvIDs)
                    .build();

            sd.setFullArtists(getSeveralArtistsRequest.execute());

            //genres sourced from recent tracks
            for(Artist currentArtist : sd.getFullArtists()){
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
            for(Artist currentArtist : sd.getArtistsLongTotal()){
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

            Map<String, Integer> finalTemp = new LinkedHashMap<>();
            int counter = 0;
            for (Map.Entry<String, Integer> entry : sortedGenres.entrySet()) {
                if (counter >= 6) {
                    break;
                }
                finalTemp.put(entry.getKey(), entry.getValue());
                counter++;
            }
            sd.setTopSixGenres(finalTemp);

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

}
