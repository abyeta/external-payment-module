package org.jala.university.infrastructure.external.endpoints;

import com.google.gson.reflect.TypeToken;

import org.jala.university.infrastructure.external.dto.customer.CustomerRegistrationRequest;
import org.jala.university.infrastructure.external.dto.customer.CustomerResponse;
import org.jala.university.infrastructure.external.config.ApiException;
import org.jala.university.infrastructure.external.config.ApiResult;
import org.jala.university.infrastructure.external.config.BaseApiClient;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Client for Customer API endpoints.
 */
public class CustomerApiClient extends BaseApiClient {

    /**
     * Retrieves a customer by identification number.
     *
     * @param identification the customer's identification
     * @return ApiResult with CustomerResponse or error
     */
    public ApiResult<CustomerResponse> getCustomerByIdentification(String identification) {
        try {
            String response = executeGet("/customers/" + identification);
            CustomerResponse customer = gson.fromJson(response, CustomerResponse.class);
            return ApiResult.success(customer);
        } catch (ApiException e) {
            return ApiResult.failure(e);
        }
    }

    /**
     * Retrieves all customers.
     *
     * @return ApiResult with list of CustomerResponse or error
     */
    public ApiResult<List<CustomerResponse>> getAllCustomers() {
        try {
            String response = executeGet("/customers");
            Type type = new TypeToken<List<CustomerResponse>>() {
            }.getType();
            List<CustomerResponse> customers = gson.fromJson(response, type);
            return ApiResult.success(customers);
        } catch (ApiException e) {
            return ApiResult.failure(e);
        }
    }

    /**
     * Creates a new customer.
     *
     * @param customer registration request data
     * @return ApiResult with created CustomerResponse or error
     */
    public ApiResult<CustomerResponse> createCustomer(CustomerRegistrationRequest customer) {
        try {
            String response = executePost("/customers", customer);
            CustomerResponse created = gson.fromJson(response, CustomerResponse.class);
            return ApiResult.success(created);
        } catch (ApiException e) {
            return ApiResult.failure(e);
        }
    }
}
