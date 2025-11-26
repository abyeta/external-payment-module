package org.jala.university.infrastructure.external.endpoints;

import com.google.gson.reflect.TypeToken;
import org.jala.university.infrastructure.external.config.ApiException;
import org.jala.university.infrastructure.external.config.ApiResult;
import org.jala.university.infrastructure.external.config.BaseApiClient;
import org.jala.university.infrastructure.external.dto.service.ServiceRegistrationRequest;
import org.jala.university.infrastructure.external.dto.service.ServiceResponse;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Client for Service API endpoints.
 */
public class ServiceApiClient extends BaseApiClient {

    /**
     * Retrieves all available services.
     *
     * @return ApiResult with list of ServiceResponse
     */
    public ApiResult<List<ServiceResponse>> getAllServices() {
        try {
            String response = executeGet("/services");
            Type type = new TypeToken<List<ServiceResponse>>() {
            }.getType();
            List<ServiceResponse> services = gson.fromJson(response, type);
            return ApiResult.success(services);
        } catch (ApiException e) {
            return ApiResult.failure(e);
        }
    }

    /**
     * Creates a new service.
     *
     * @param service registration request data
     * @return ApiResult with created ServiceResponse
     */
    public ApiResult<ServiceResponse> createService(ServiceRegistrationRequest service) {
        try {
            String response = executePost("/services", service);
            ServiceResponse serviceResponse = gson.fromJson(response, ServiceResponse.class);
            return ApiResult.success(serviceResponse);
        } catch (ApiException e) {
            return ApiResult.failure(e);
        }
    }
}
