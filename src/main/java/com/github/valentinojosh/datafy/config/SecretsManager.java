package com.github.valentinojosh.datafy.config;

public interface SecretsManager {
    String fetchSecret(String secretName);
}