package com.github.valentinojosh.datafy.config;

import io.github.cdimascio.dotenv.Dotenv;

public class MockSecretsManager implements SecretsManager {

    @Override
    public String fetchSecret(String secretName) {
        //Local
        Dotenv dotenv = Dotenv.load();
        return dotenv.get(secretName);

        //Docker
        //return System.getenv(secretName);
    }
}
