package org.jala.university.infrastructure.external.endpoints;

import com.google.gson.reflect.TypeToken;
import org.jala.university.infrastructure.external.dto.subscription.SubscriptionRequest;
import org.jala.university.infrastructure.external.dto.subscription.SubscriptionResponse;
import org.jala.university.infrastructure.external.config.ApiException;
import org.jala.university.infrastructure.external.config.ApiResult;
import org.jala.university.infrastructure.external.config.BaseApiClient;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Client for Subscription API endpoints.
 */
public class SubscriptionApiClient extends BaseApiClient {

    /**
     * Gets a subscription by its payment code.
     *
     * @param paymentCode the subscription payment code
     * @return ApiResult with SubscriptionResponse
     */
    public ApiResult<SubscriptionResponse> getSubscriptionByPaymentCode(String paymentCode) {
        try {
            String response = executeGet("/subscriptions/" + paymentCode);
            SubscriptionResponse subscription = gson.fromJson(response, SubscriptionResponse.class);
            return ApiResult.success(subscription);
        } catch (ApiException e) {
            return ApiResult.failure(e);
        }
    }

    /**
     * Gets all subscriptions for a customer.
     *
     * @param identification customer identification
     * @return ApiResult with list of SubscriptionResponse
     */
    public ApiResult<List<SubscriptionResponse>> getSubscriptionsByCustomer(String identification) {
        try {
            String response = executeGet("/subscriptions/by-customer/" + identification);
            Type type = new TypeToken<List<SubscriptionResponse>>() {
            }.getType();
            List<SubscriptionResponse> subscriptions = gson.fromJson(response, type);
            return ApiResult.success(subscriptions);
        } catch (ApiException e) {
            return ApiResult.failure(e);
        }
    }

    /**
     * Creates a new subscription.
     *
     * @param subscription subscription request data
     * @return ApiResult with created SubscriptionResponse
     */
    public ApiResult<SubscriptionResponse> createSubscription(SubscriptionRequest subscription) {
        try {
            String response = executePost("/subscriptions", subscription);
            SubscriptionResponse created = gson.fromJson(response, SubscriptionResponse.class);
            return ApiResult.success(created);
        } catch (ApiException e) {
            return ApiResult.failure(e);
        }
    }
}
