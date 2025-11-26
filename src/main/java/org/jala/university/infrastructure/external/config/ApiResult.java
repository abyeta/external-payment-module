package org.jala.university.infrastructure.external.config;

/**
 * Simple wrapper for API call results. Represents either a successful response with data
 * or a failed response with an error. Used by all API clients.
 *
 * @param <T> the type of the data returned on success
 */

public final class ApiResult<T> {
    private final T data;
    private final ApiException error;
    private final boolean success;


    private ApiResult(T data, ApiException error, boolean success) {
        this.data = data;
        this.error = error;
        this.success = success;
    }

    /**
     * Creates a successful result.
     *
     * @param data the response data
     * @param <T>  type of the data
     * @return successful ApiResult instance
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(data, null, true);
    }

    /**
     * Creates a failed result.
     *
     * @param error the exception that occurred
     * @param <T>   type of the expected data (will be null)
     * @return failed ApiResult instance
     */
    public static <T> ApiResult<T> failure(ApiException error) {
        return new ApiResult<>(null, error, false);
    }

    /**
     * @return true if the operation was successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return true if the operation failed
     */
    public boolean isFailure() {
        return !success;
    }

    /**
     * @return the result data (null if failed)
     */
    public T getData() {
        return data;
    }

    /**
     * @return the error (null if successful)
     */
    public ApiException getError() {
        return error;
    }
}
