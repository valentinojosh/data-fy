package com.github.valentinojosh.datafy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

@Component
public class SpotifyApiFactory {
    private final SecretsManager secretsManager;
    private final String clientId;
    private final String redirectUri;

    public SpotifyApiFactory(
            SecretsManager secretsManager,
            @Value("${spotify.client-id}") String clientId,
            @Value("${spotify.redirect-uri}") String redirectUri) {
        this.secretsManager = secretsManager;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
    }

    public SpotifyApi create() {
        String clientSecret = secretsManager.fetchSecret("CLIENT_SECRET");
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalStateException("Spotify client credentials are not configured");
        }

        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(SpotifyHttpManager.makeUri(redirectUri))
                .build();
    }
}
