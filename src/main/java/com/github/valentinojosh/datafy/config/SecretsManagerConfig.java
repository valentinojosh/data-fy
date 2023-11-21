package com.github.valentinojosh.datafy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SecretsManagerConfig {
    @Bean
    @Profile("production")
    public SecretsManager realSecretsManager() {
        return new GCSMSecretsManager();
    }

    @Bean
    @Profile("development")
    public SecretsManager localSecretsManager() {
        return new LocalSecretsManager();
    }

    @Bean
    @Profile("docker")
    public SecretsManager dockerSecretsManager() {
        return new DockerSecretsManager();
    }
}
