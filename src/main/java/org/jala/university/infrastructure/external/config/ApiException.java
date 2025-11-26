package org.jala.university.infrastructure.external.config;

public class ApiException extends Exception {
    private int statusCode;

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
