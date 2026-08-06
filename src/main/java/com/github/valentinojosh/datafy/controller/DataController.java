package com.github.valentinojosh.datafy.controller;
import com.github.valentinojosh.datafy.config.SpotifyApiFactory;
import com.github.valentinojosh.datafy.object.SpotifyData;
import com.github.valentinojosh.datafy.service.ListeningMinutesEstimator;
import jakarta.annotation.PreDestroy;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.artists.GetSeveralArtistsRequest;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import se.michaelthelin.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetSeveralTracksRequest;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${cors.origin}")
public class DataController {
    private final SpotifyApiFactory spotifyApiFactory;
    private final ListeningMinutesEstimator listeningMinutesEstimator;

    @Autowired
    public DataController(
            SpotifyApiFactory spotifyApiFactory,
            ListeningMinutesEstimator listeningMinutesEstimator) {
        this.spotifyApiFactory = spotifyApiFactory;
        this.listeningMinutesEstimator = listeningMinutesEstimator;
    }

    private final ExecutorService executorService = Executors.newFixedThreadPool(2); // 2 Threads

    @PreDestroy
    public void onDestroy() {
        executorService.shutdown();
    }

    @GetMapping("/data")
    public SpotifyData handleData(@RequestHeader("Authorization") String authorizationHeader) {
        final SpotifyData sd = new SpotifyData();
        String accessToken = extractBearerToken(authorizationHeader).orElse(null);

        if (accessToken != null) {
            SpotifyApi spotifyApi = spotifyApiFactory.create();
            if (sd.getMinutes() == 0) {
                try {
                    processData(accessToken, sd, spotifyApi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return sd;
        }
        return null;
    }

    static Optional<String> extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        String token = authorizationHeader.substring(7).trim();
        if (token.length() >= 2 && token.startsWith("\"") && token.endsWith("\"")) {
            token = token.substring(1, token.length() - 1);
        }
        return token.isBlank() ? Optional.empty() : Optional.of(token);
    }

    public void processData(String accessToken, SpotifyData sd, SpotifyApi spotifyApi){
        spotifyApi.setAccessToken(accessToken);

        // Fetch the pages used by the minutes estimate while other Spotify requests run.
        //Note: In GCP Standard App Engine Environment, background threads have restrictions: including the thread can't outlive the request that spawned them.
        Future<?> future = executorService.submit(() -> {
            try {
                sd.setMinutes(listeningMinutesEstimator.estimateAnnualMinutes((offset, limit) -> {
                    Track[] tracks = spotifyApi.getUsersTopTracks()
                            .time_range("long_term")
                            .limit(limit)
                            .offset(offset)
                            .build()
                            .execute()
                            .getItems();
                    int[] durations = new int[tracks.length];
                    for (int index = 0; index < tracks.length; index++) {
                        durations[index] = tracks[index] == null ? 0 : tracks[index].getDurationMs();
                    }
                    return durations;
                }));
            } catch (Exception e) {
                sd.setMinutes(0);
                System.out.println("Error estimating listening minutes: " + e.getMessage());
            }
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

    private void getSpotifyDataRec(SpotifyData sd, SpotifyApi spotifyApi) {
        if (sd.getTracksShort() == null || sd.getTracksShort().length == 0) {
            sd.setRecommendations(new Track[0]);
            return;
        }
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
