package com.github.valentinojosh.datafy.config;

public class MockSecretsManager implements SecretsManager {

    @Override
    public String fetchSecret(String secretName) {
        // This mock just returns a hardcoded value or reads from environment variable or local .env.
        // Adjust this logic as per your needs.
        return System.getenv(secretName);
    }
}
