package org.jala.university.infrastructure.external.config;

public enum Environment {
    DEV("http://localhost:3000/external-service/api/v1"),
    PROD("https://external-services-my63.onrender.com/external-service/api/v1");

    private final String baseUrl;

    Environment(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
