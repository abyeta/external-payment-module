package org.jala.university.infrastructure.external.config;

public final class ApiConfig {

    private static Environment currentEnvironment = Environment.DEV;

    private ApiConfig() {
    }

    public static void setEnvironment(Environment environment) {
        currentEnvironment = environment;
    }

    public static Environment getCurrentEnvironment() {
        return currentEnvironment;
    }

    public static String getBaseUrl() {
        return currentEnvironment.getBaseUrl();
    }

}
