package com.github.valentinojosh.datafy.service;

import com.github.valentinojosh.datafy.config.SecretsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecretsService {

    private final SecretsManager secretsManager;

    // Constructor injection is recommended
    @Autowired
    public SecretsService(SecretsManager secretsManager) {
        this.secretsManager = secretsManager;
    }

    public void someMethod() {
        String secret = secretsManager.fetchSecret("my-secret-key");
        // Use the secret
    }
}

