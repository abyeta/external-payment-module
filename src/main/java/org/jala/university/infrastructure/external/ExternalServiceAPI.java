package org.jala.university.infrastructure.external;

import org.jala.university.application.dto.CustomerDto;
import org.jala.university.application.dto.ExternalServiceRegistrationRequestDto;
import org.jala.university.infrastructure.external.config.ApiResult;
import org.jala.university.infrastructure.external.dto.customer.CustomerRegistrationRequest;
import org.jala.university.infrastructure.external.dto.customer.CustomerResponse;
import org.jala.university.infrastructure.external.dto.invoice.InvoiceResponse;
import org.jala.university.infrastructure.external.dto.service.ServiceRegistrationRequest;
import org.jala.university.infrastructure.external.dto.service.ServiceResponse;
import org.jala.university.infrastructure.external.dto.subscription.SubscriptionRequest;
import org.jala.university.infrastructure.external.dto.subscription.SubscriptionResponse;
import org.jala.university.presentation.GlobalContext;

import java.util.List;

/**
 * Utility class for registering external services and automatically generating
 * the corresponding subscription and initial invoice in the external payment API.
 */
public final class ExternalServiceAPI {

    public static final String RESET = "\u001B[0m";
    public static final String YELLOW = "\u001B[93m";
    public static final String BLUE = "\u001B[94m";
    private static final int REPEAT_EQUAL = 30;

    private ExternalServiceAPI() {
    }

    /**
     * Registers a new external service.
     *
     * @param request registration data for the external service
     */
    public static void registerExternalService(ExternalServiceRegistrationRequestDto request) {
        ApiResult<ServiceResponse> service = generateService(request);

        if (service.isSuccess()) {
            String serviceCode = service.getData().getCode();
            String identification = checkCustomerIdentification(createDefaultCustomer());

            String subscriptionPaymentCode = generateSubscription(identification, serviceCode);

            InvoiceResponse invoice = ApiClientFactory.invoices().createInvoice(subscriptionPaymentCode).getData();

            showSubscriptionDetails(invoice.getSubscription());
        }
    }

    /**
     * Builds the predefined global default customer.
     *
     * @return default customer registration request
     */
    public static CustomerRegistrationRequest getDefaultCustomer() {
        return CustomerRegistrationRequest.builder()
                .name("Juanito Monroe")
                .identification("10101010")
                .email("juan@gmail.com")
                .build();
    }

    /**
     * @return default customer ID
     */
    public static Long getDefaultCustomerId() {
        CustomerDto customerDto = CustomerDto.builder()
                .id(1L)
                .build();
        GlobalContext.getInstance().setCustomer(customerDto);
        return 1L;
    }

    /**
     * Prints the key details of a subscription to the console.
     */
    public static void getAllCustomerSubscriptions() {
        ApiResult<List<SubscriptionResponse>> subscription = ApiClientFactory.subscriptions()
                .getSubscriptionsByCustomer(getDefaultCustomer().getIdentification());

        if (subscription.isSuccess()) {
            subscription.getData().forEach(ExternalServiceAPI::showSubscriptionDetails);
        }
    }

    /**
     * Creates the service in the external API.
     *
     * @param request source registration data
     * @return ApiResult containing the created service
     */
    private static ApiResult<ServiceResponse> generateService(ExternalServiceRegistrationRequestDto request) {
        return ApiClientFactory.services()
                .createService(ServiceRegistrationRequest.builder()
                        .name(request.getProviderName())
                        .code(request.getAccountReference())
                        .build());
    }

    /**
     * Creates a subscription linking a customer and a service.
     *
     * @param customerIdentification customer identification number
     * @param serviceCode            code of the registered service
     * @return payment code of the created subscription
     */
    private static String generateSubscription(String customerIdentification, String serviceCode) {
        return ApiClientFactory.subscriptions()
                .createSubscription(SubscriptionRequest.builder()
                        .serviceCode(serviceCode)
                        .customerIdentification(customerIdentification)
                        .build()).getData().getPaymentCode();
    }

    /**
     * Ensures a customer identification exists.
     * Falls back to a global default customer if creation fails or returns null.
     *
     * @param customer result of customer creation attempt
     * @return valid customer identification
     */
    private static String checkCustomerIdentification(ApiResult<CustomerResponse> customer) {
        return customer.getData() == null
                ? getDefaultCustomer().getIdentification()
                : customer.getData().getIdentification();
    }

    /**
     * Attempts to create the default global customer in the external API.
     *
     * @return ApiResult with the created customer or error
     */
    private static ApiResult<CustomerResponse> createDefaultCustomer() {
        return ApiClientFactory.customers().createCustomer(getDefaultCustomer());
    }

    /**
     * Prints the key details of a generated subscription to the console with colored output.
     * Used for quick feedback during service registration.
     *
     * @param subscription the created subscription response from the external API
     */
    private static void showSubscriptionDetails(SubscriptionResponse subscription) {
        System.out.println(YELLOW + "Subscripcion:" + RESET);
        System.out.println(BLUE + "Codigo de pago:" + RESET + subscription.getPaymentCode());
        System.out.println(BLUE + "Identificacion cliente:" + RESET + subscription
                .getCustomer().getIdentification());
        System.out.println(BLUE + "Codigo Servicio:" + RESET + subscription.getService().getCode());
        System.out.println("=".repeat(REPEAT_EQUAL));
    }
}
