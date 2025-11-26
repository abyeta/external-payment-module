package org.jala.university.infrastructure.external.endpoints;

import com.google.gson.reflect.TypeToken;
import org.jala.university.infrastructure.external.config.ApiException;
import org.jala.university.infrastructure.external.config.ApiResult;
import org.jala.university.infrastructure.external.config.BaseApiClient;
import org.jala.university.infrastructure.external.dto.invoice.InvoiceResponse;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Client for Invoice API endpoints.
 */
public class InvoiceApiClient extends BaseApiClient {

    /**
     * Gets all pending invoices for a given payment code.
     *
     * @param paymentCode the subscription payment code
     * @return ApiResult with list of invoices
     */
    public ApiResult<List<InvoiceResponse>> getPendingInvoicesByPaymentCode(String paymentCode) {
        try {
            String response = executeGet("/invoices/by-payment-code/" + paymentCode);
            Type type = new TypeToken<List<InvoiceResponse>>() {
            }.getType();
            List<InvoiceResponse> invoices = gson.fromJson(response, type);
            return ApiResult.success(invoices);
        } catch (ApiException e) {
            return ApiResult.failure(e);
        }
    }

    /**
     * Gets all pending invoices for a customer.
     *
     * @param identification customer identification
     * @return ApiResult with list of invoices
     */
    public ApiResult<List<InvoiceResponse>> getPendingInvoicesByCustomer(String identification) {
        try {
            String response = executeGet("/invoices/by-customer/" + identification);
            Type type = new TypeToken<List<InvoiceResponse>>() {
            }.getType();
            List<InvoiceResponse> invoices = gson.fromJson(response, type);
            return ApiResult.success(invoices);
        } catch (ApiException e) {
            return ApiResult.failure(e);
        }
    }

    /**
     * Pays a specific invoice.
     *
     * @param invoiceNumber invoice number to pay
     * @return ApiResult with the paid invoice
     */
    public ApiResult<InvoiceResponse> payInvoice(Long invoiceNumber) {
        try {
            String response = executePost("/invoices/pay/" + invoiceNumber, null);
            InvoiceResponse invoice = gson.fromJson(response, InvoiceResponse.class);
            return ApiResult.success(invoice);
        } catch (ApiException e) {
            return ApiResult.failure(e);
        }
    }

    /**
     * Creates a new invoice for a subscription payment code.
     *
     * @param subscriptionPaymentCode payment code of the subscription
     * @return ApiResult with the created invoice
     */
    public ApiResult<InvoiceResponse> createInvoice(String subscriptionPaymentCode) {
        try {
            String response = executePost("/invoices/" + subscriptionPaymentCode, null);
            InvoiceResponse invoice = gson.fromJson(response, InvoiceResponse.class);
            return ApiResult.success(invoice);
        } catch (ApiException e) {
            return ApiResult.failure(e);
        }
    }

    /**
     * Gets only paid invoices for a payment code.
     *
     * @param paymentCode the subscription payment code
     * @return ApiResult with list of paid invoices
     */
    public ApiResult<List<InvoiceResponse>> getPaidInvoiceByPaymentCode(String paymentCode) {
        try {
            String response = executeGet("/invoices/by-payment-code/" + paymentCode + "/paid");
            Type type = new TypeToken<List<InvoiceResponse>>() {
            }.getType();
            List<InvoiceResponse> invoice = gson.fromJson(response, type);
            return ApiResult.success(invoice);
        } catch (ApiException e) {
            return ApiResult.failure(e);
        }
    }

    /**
     * Gets only paid invoices for a customer.
     *
     * @param identification customer identification
     * @return ApiResult with list of paid invoices
     */
    public ApiResult<List<InvoiceResponse>> getPaidInvoiceByCustomer(String identification) {
        try {
            String response = executeGet("/invoices/by-customer/" + identification + "/paid");
            Type type = new TypeToken<List<InvoiceResponse>>() {
            }.getType();
            List<InvoiceResponse> invoice = gson.fromJson(response, type);
            return ApiResult.success(invoice);
        } catch (ApiException e) {
            return ApiResult.failure(e);
        }
    }

}
