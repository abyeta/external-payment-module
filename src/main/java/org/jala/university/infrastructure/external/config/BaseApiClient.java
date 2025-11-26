package org.jala.university.infrastructure.external.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Base class for all external API clients.
 *
 * Provides common HTTP functionality (GET/POST), JSON serialization with proper {@link LocalDate} support,
 * and centralized error handling.
 */
public abstract class BaseApiClient {

    protected static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    protected final OkHttpClient client;
    protected final Gson gson;
    protected final String baseUrl;

    public BaseApiClient() {
        this.client = new OkHttpClient();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .create();
        this.baseUrl = ApiConfig.getBaseUrl();
    }

    /**
     * Executes a GET request to the specified endpoint.
     *
     * @param endpoint the API endpoint (relative path)
     * @return raw JSON response as String
     * @throws ApiException if request fails or response is not successful
     */
    protected String executeGet(String endpoint) throws ApiException {
        String url = baseUrl + endpoint;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        return executeRequest(request);
    }

    /**
     * Executes a POST request with JSON body.
     *
     * @param endpoint the API endpoint (relative path)
     * @param body     object to serialize as JSON (can be null for empty body)
     * @return raw JSON response as String
     * @throws ApiException if request fails or response is not successful
     */
    protected String executePost(String endpoint, Object body) throws ApiException {
        String url = baseUrl + endpoint;
        String json = gson.toJson(body);

        RequestBody requestBody = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        return executeRequest(request);
    }

    /**
     * Executes the HTTP request and handles response.
     *
     * @param request the built OkHttp request
     * @return response body as String
     * @throws ApiException on network error or non-2xx response
     */
    private String executeRequest(Request request) throws ApiException {
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            if (!response.isSuccessful()) {
                throw handleErrorResponse(response.code(), responseBody);
            }

            return responseBody;

        } catch (IOException e) {
            throw new ApiException("Connection error: " + e.getMessage(), e);
        }
    }

    /**
     * Converts error responses into meaningful {@link ApiException}.
     *
     * @param statusCode    HTTP status code
     * @param responseBody  raw error response
     * @return formatted ApiException
     */
    private ApiException handleErrorResponse(int statusCode, String responseBody) {
        try {
            JsonObject error = gson.fromJson(responseBody, JsonObject.class);
            String message = error.has("message") ? error.get("message").getAsString() : "Unknown error";
            return new ApiException(statusCode, message);
        } catch (Exception e) {
            return new ApiException(statusCode, "Error: " + responseBody);
        }
    }
}
