package com.github.valentinojosh.datafy.config;

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;

public class GCSMSecretsManager implements SecretsManager {

    @Override
    public String fetchSecret(String secretName) {
        // Fetch secret using GCSM SDK
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            SecretVersionName secretVersionName = SecretVersionName.of("[YOUR_PROJECT_ID]", secretName, "[SECRET_VERSION]");
            return client.accessSecretVersion(secretVersionName).getPayload().getData().toStringUtf8();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch secret: " + secretName, e);
        }
    }
}
