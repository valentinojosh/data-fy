package com.github.valentinojosh.datafy.config;

public class MockSecretsManager implements SecretsManager {

    @Override
    public String fetchSecret(String secretName) {
        return System.getenv(secretName);
    }
}
