package com.github.valentinojosh.datafy.controller;
import com.github.valentinojosh.datafy.config.SecretsManager;
import com.github.valentinojosh.datafy.object.SpotifyData;
import jakarta.annotation.PreDestroy;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
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
//https://data-fy.netlify.app/
//http://localhost:3000/

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000/", allowCredentials = "true")
public class DataController {
    private final SecretsManager secretsManager;

    @Autowired
    public DataController(SecretsManager secretsManager) {
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

    private final ExecutorService executorService = Executors.newFixedThreadPool(2); // 2 Threads

    @PreDestroy
    public void onDestroy() {
        executorService.shutdown();
    }

    @GetMapping("/data")
    public SpotifyData handleData(@RequestHeader("Authorization") String authorizationHeader) {
        SpotifyApi spotifyApi = createSpotifyApi();


        final SpotifyData sd = new SpotifyData();
        String accessToken = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7).replace("\"", "");;
        }

        if (accessToken != null){
            if(sd.getMinutes() == 0){
                try {
                    processData(accessToken, sd, spotifyApi);
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

    public void processData(String accessToken, SpotifyData sd, SpotifyApi spotifyApi){
        spotifyApi.setAccessToken(accessToken);

        //Invoke separate thread for getting minutes as it is a recursive call and takes the longest of all the methods
        //Note: In GCP Standard App Engine Environment, background threads have restrictions: including the thread can't outlive the request that spawned them.
        Future<?> future = executorService.submit(() -> {
            sd.setMinutes(0);

            getTotalMinutes(0, sd,0,spotifyApi);

            // Ms/60000 = Minutes per week * 52 weeks in a year = est minutes per year
            sd.setMinutes((sd.getMinutes()/60000)*52);
        });

        getTopArtists(sd,spotifyApi);
        getTopGenres(sd,spotifyApi);
        getTopTracks(sd,spotifyApi);
        getSpotifyDataRec(sd,spotifyApi);

        try {
            future.get();  // this will block until the submitted task is complete
        } catch (Exception e) {
            // Handle any exception thrown during the execution of the task
            e.printStackTrace();
        }
    }

    private void getTopArtists(SpotifyData sd, SpotifyApi spotifyApi) {
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
            sd.setArtistsShort(null);
            sd.setArtistsMedium(null);
            sd.setArtistsLong(null);
            sd.setArtistsLongTotal(null);
            System.out.println("Error getting top artists: " + e.getMessage());
        }
    }

    private void getTopGenres(SpotifyData sd, SpotifyApi spotifyApi) {
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
            sd.setTopSixGenres(null);
            System.out.println("Error getting top genres: " + e.getMessage());
        }

    }

    private void getTopTracks(SpotifyData sd, SpotifyApi spotifyApi) {
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
            sd.setTracksShort(null);
            sd.setTracksMedium(null);
            sd.setTracksLong(null);
            System.out.println("Error getting top tracks: " + e.getMessage());
        }
    }

    private void getTotalMinutes(int offset, SpotifyData sd, int numTracksCounted, SpotifyApi spotifyApi) {
        //350 tracks is one week (estimated) assuming maximal listening, capping the track search there
        if(numTracksCounted >= 350){
            return;
        }

        final GetUsersTopTracksRequest getUsersTopTracksRequestOne = spotifyApi.getUsersTopTracks()
                .time_range("long_term")
                .limit(50)
                .offset(offset)
                .build();
        try{
            final Track[] one = getUsersTopTracksRequestOne.execute().getItems();

            float weekTotalMinutes = 0;
            for(Track t : one){
                weekTotalMinutes += t.getDurationMs();
            }
            sd.setMinutes(sd.getMinutes()+weekTotalMinutes);

            if(one.length % 50 == 0 & one.length > 0){
                numTracksCounted += 50;
                getTotalMinutes(one.length-1,sd,numTracksCounted,spotifyApi);
            }
        } catch (Exception e){
            sd.setMinutes(0);
            System.out.println("Error getting top tracks: " + e.getMessage());
        }
    }

    private void getSpotifyDataRec(SpotifyData sd, SpotifyApi spotifyApi) {
        List<String> trackIds = new ArrayList<>();
        for(Track t : sd.getTracksShort()){
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
