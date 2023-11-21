package com.github.valentinojosh.datafy.config;

public class DockerSecretsManager implements SecretsManager {

    @Override
    public String fetchSecret(String secretName) {
        return System.getenv(secretName);
    }
}