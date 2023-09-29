package com.github.valentinojosh.datafy.controller;

import com.github.valentinojosh.datafy.object.SpotifyData;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PreDestroy;
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
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import se.michaelthelin.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetSeveralTracksRequest;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
    private int initialX = 0;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectUri)
            .build();

    @PreDestroy
    public void onDestroy() {
        executorService.shutdown();
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @GetMapping("/data")
    public SpotifyData handleData(HttpSession session) throws IOException {
        if (Boolean.TRUE.equals(session.getAttribute("isAuth"))){
            if(sd.getMinutes() == 0){
                try {
                    processData(session);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            return sd;
        }
        else{
            return null;
        }
    }

    public void processData(HttpSession session){
        String accessToken = (String) session.getAttribute("accessToken");
        String refreshToken = (String) session.getAttribute("refreshToken");
        spotifyApi.setAccessToken(accessToken);
        spotifyApi.setRefreshToken(refreshToken);

        //Invoke separate thread for getting minutes as it is a recursive call and takes the longest of all the methods
        Future<?> future = executorService.submit(() -> {
            sd.setMinutes(0);
            initialX = 0;
            getTotalMinutes(initialX);

            // Ms/60000 = Minutes per week * 52 weeks in a year = est minutes per year
            sd.setMinutes((sd.getMinutes()/60000)*52);
        });

        getTopArtists();
        getTopGenres();
        getTopTracks();
        getSpotifyDataRec(sd);

        try {
            future.get();  // this will block until the submitted task is complete
        } catch (Exception e) {
            // Handle any exception thrown during the execution of the task
            e.printStackTrace();
        }
    }

    private void getTopArtists() {
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
        //Start by grabbing max limit top artists. then populate through hashmap by genre. weigh these more heavily then max limit of recents
        //Next get the max limit of most recent tracks played. add into the same previous hashmap by genre
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
                        genres.put(currentGenre, (genres.get(currentGenre) + 4));
                    } else {
                        genres.put(currentGenre, 4);
                    }
                }
            }

            //genres sourced from top artists weighted more heavily than top songs
            for(Artist currentArtist : sd.getArtistsLongTotal()){
                String[] genre = currentArtist.getGenres();
                for(String currentGenre : genre){
                    if (genres.containsKey(currentGenre)) {
                        genres.put(currentGenre, genres.get(currentGenre) + 8);
                    } else {
                        genres.put(currentGenre, 8);
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

    private void getTopTracks() {
        final GetUsersTopTracksRequest getUsersTopTracksRequestShort = spotifyApi.getUsersTopTracks()
                .time_range("short_term")
                .limit(5)
                .build();

        final  GetUsersTopTracksRequest getUsersTopTracksRequestMedium = spotifyApi.getUsersTopTracks()
                .time_range("medium_term")
                .limit(5)
                .build();

        final  GetUsersTopTracksRequest getUsersTopTracksRequestLong = spotifyApi.getUsersTopTracks()
                .time_range("long_term")
                .limit(5)
                .build();

        try{
            sd.setTracksShort(getUsersTopTracksRequestShort.execute().getItems());
            sd.setTracksMedium(getUsersTopTracksRequestMedium.execute().getItems());
            sd.setTracksLong(getUsersTopTracksRequestLong.execute().getItems());
        } catch (Exception e){
            System.out.println("Error getting top tracks: " + e.getMessage());
        }
    }

    private void getTotalMinutes(int x) {
        //350 tracks is one week assuming maximal listening, capping the track search there
        if(initialX >= 350){
            return;
        }

        final GetUsersTopTracksRequest getUsersTopTracksRequestOne = spotifyApi.getUsersTopTracks()
                .time_range("long_term")
                .limit(50)
                .offset(x)
                .build();
        try{
            final Track[] one = getUsersTopTracksRequestOne.execute().getItems();

            for(Track t : one){
                sd.setMinutes(sd.getMinutes()+t.getDurationMs());
            }

            if(one.length % 50 == 0 & one.length > 0){
                initialX += 50;
                getTotalMinutes(one.length-1);
            }
        } catch (Exception e){
            System.out.println("Error getting top tracks: " + e.getMessage());
        }
    }

    private void getSpotifyDataRec(SpotifyData obj) {
        List<String> trackIds = new ArrayList<>();
        for(Track t : obj.getTracksShort()){
            trackIds.add(t.getId());
        }
        String trackSeeds = String.join(",", trackIds);

        final GetRecommendationsRequest getRecommendationsRequest = spotifyApi.getRecommendations()
                .seed_tracks(trackSeeds)
                .limit(5)
                .build();

        try {
            //Get Recommendations
            final Recommendations recommendations = getRecommendationsRequest.execute();

            //Translate Recommendation tracks (simplified) to normal tracks
            List<String> recTrackIds = new ArrayList<>();
            for(TrackSimplified ts : recommendations.getTracks()){
                recTrackIds.add(ts.getId());
            }
            String recTrackSeeds = String.join(",", recTrackIds);
            final GetSeveralTracksRequest getSeveralTracksRequest = spotifyApi.getSeveralTracks(recTrackSeeds)
                    .build();

            try{
                sd.setRecommendations(getSeveralTracksRequest.execute());
            }
            catch (IOException | SpotifyWebApiException | ParseException e) {
                System.out.println("Error: " + e.getMessage());
            }

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

}
