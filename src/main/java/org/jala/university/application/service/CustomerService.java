package org.jala.university.application.service;

import org.jala.university.application.dto.ExternalServiceDto;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing customer-service links.
 * Handles searching for services and linking/unlinking them to customers.
 */
public interface CustomerService {

    /**
     * Gets all services linked to a specific customer.
     *
     * @param customerId the customer ID
     * @return list of linked services
     */
    List<ExternalServiceDto> getLinkedServices(Long customerId);

    /**
     * Links an external service to a customer.
     *
     * @param customerId the customer ID
     * @param serviceId the service ID to link
     * @throws IllegalArgumentException if customer or service not found
     * @throws IllegalStateException if service is not enabled
     */
    void linkService(Long customerId, UUID serviceId);

    /**
     * Unlinks an external service from a customer.
     *
     * @param customerId the customer ID
     * @param serviceId the service ID to unlink
     */
    void unlinkService(Long customerId, UUID serviceId);

    /**
     * Checks if a service is already linked to a customer.
     *
     * @param customerId the customer ID
     * @param serviceId the service ID
     * @return true if linked, false otherwise
     */
    boolean isServiceLinked(Long customerId, UUID serviceId);
}


