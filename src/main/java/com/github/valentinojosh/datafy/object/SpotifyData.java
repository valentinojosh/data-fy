package com.github.valentinojosh.datafy.object;

import se.michaelthelin.spotify.model_objects.specification.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SpotifyData {
    private Artist[] artistsShort;
    private Artist[] artistsMedium;
    private Artist[] artistsLong;
    private Artist[] artistsLongTotal;
    private PlayHistory[] recentTrackTotal;
    private ArrayList<ArtistSimplified> recentArtists = new ArrayList<>();
    private Artist[] fullArtists;
    private Map<String, Integer> topSixGenres = new LinkedHashMap<>();
    private Track[] tracksShort;
    private Track[] tracksMedium;
    private Track[] tracksLong;
    private Track[] recommendations;
    private float minutes;


    public SpotifyData(){
        // No-argument constructor
    }

    public Artist[] getArtistsShort() {
        return artistsShort;
    }

    public void setArtistsShort(Artist[] artistsShort) {
        this.artistsShort = artistsShort;
    }

    public Artist[] getArtistsMedium() {
        return artistsMedium;
    }

    public void setArtistsMedium(Artist[] artistsMedium) {
        this.artistsMedium = artistsMedium;
    }

    public Artist[] getArtistsLong() {
        return artistsLong;
    }

    public void setArtistsLong(Artist[] artistsLong) {
        this.artistsLong = artistsLong;
    }

    public Artist[] getArtistsLongTotal() {
        return artistsLongTotal;
    }

    public void setArtistsLongTotal(Artist[] artistsLongTotal) {
        this.artistsLongTotal = artistsLongTotal;
    }

    public PlayHistory[] getRecentTrackTotal() {
        return recentTrackTotal;
    }

    public void setRecentTrackTotal(PlayHistory[] recentTrackTotal) {
        this.recentTrackTotal = recentTrackTotal;
    }

    public ArrayList<ArtistSimplified> getRecentArtists() {
        return recentArtists;
    }

    public void setRecentArtists(ArrayList<ArtistSimplified> recentArtists) {
        this.recentArtists = recentArtists;
    }

    public Artist[] getFullArtists() {
        return fullArtists;
    }

    public void setFullArtists(Artist[] fullArtists) {
        this.fullArtists = fullArtists;
    }

    public Map<String, Integer> getTopSixGenres() {
        return topSixGenres;
    }

    public void setTopSixGenres(Map<String, Integer> topSixGenres) {
        this.topSixGenres = topSixGenres;
    }

    public Track[] getTracksShort() {
        return tracksShort;
    }

    public void setTracksShort(Track[] tracksShort) {
        this.tracksShort = tracksShort;
    }

    public Track[] getTracksMedium() {
        return tracksMedium;
    }

    public void setTracksMedium(Track[] tracksMedium) {
        this.tracksMedium = tracksMedium;
    }

    public Track[] getTracksLong() {
        return tracksLong;
    }

    public void setTracksLong(Track[] tracksLong) {
        this.tracksLong = tracksLong;
    }

    public Track[] getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(Track[] recommendations) {
        this.recommendations = recommendations;
    }

    public float getMinutes() {
        return minutes;
    }

    public void setMinutes(float minutes) {
        this.minutes = minutes;
    }
}