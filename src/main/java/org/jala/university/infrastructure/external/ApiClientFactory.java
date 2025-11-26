package org.jala.university.infrastructure.external;

import org.jala.university.infrastructure.external.endpoints.CustomerApiClient;
import org.jala.university.infrastructure.external.endpoints.InvoiceApiClient;
import org.jala.university.infrastructure.external.endpoints.ServiceApiClient;
import org.jala.university.infrastructure.external.endpoints.SubscriptionApiClient;

/**
 * Simple factory to access all external API clients in a clean way.
 * Provides short, readable methods to get each injected client.
 */
public final class ApiClientFactory {

    private static final ServiceApiClient SERVICE_CLIENT = new ServiceApiClient();
    private static final InvoiceApiClient INVOICE_CLIENT = new InvoiceApiClient();
    private static final CustomerApiClient CUSTOMER_CLIENT = new CustomerApiClient();
    private static final SubscriptionApiClient SUBSCRIPTION_CLIENT = new SubscriptionApiClient();

    private ApiClientFactory() {
    }

    /**
     * @return the Service API client
     */
    public static ServiceApiClient services() {
        return SERVICE_CLIENT;
    }

    /**
     * @return the Invoice API client
     */
    public static InvoiceApiClient invoices() {
        return INVOICE_CLIENT;
    }

    /**
     * @return the Customer API client
     */
    public static CustomerApiClient customers() {
        return CUSTOMER_CLIENT;
    }

    /**
     * @return the Subscription API client
     */
    public static SubscriptionApiClient subscriptions() {
        return SUBSCRIPTION_CLIENT;
    }
}
