package org.jala.university.domain.repository;

import org.jala.university.commons.domain.repository.Repository;
import org.jala.university.domain.entity.Customer;
import org.jala.university.domain.entity.ExternalService;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Customer entity operations.
 * Extends the base Repository interface for basic CRUD operations.
 */
public interface CustomerRepository extends Repository<Customer, UUID> {

    List<ExternalService> getLinkedServices(UUID customerId);
    void linkService(UUID customerId, UUID serviceId);
    void unlinkService(UUID customerId, UUID serviceId);
    boolean isServiceLinked(UUID customerId, UUID serviceId);
}


