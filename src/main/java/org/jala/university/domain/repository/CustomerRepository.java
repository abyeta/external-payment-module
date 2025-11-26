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
public interface CustomerRepository extends Repository<Customer, Long> {

    List<ExternalService> getLinkedServices(Long customerId);
    void linkService(Long customerId, UUID serviceId);
    void unlinkService(Long customerId, UUID serviceId);
    boolean isServiceLinked(Long customerId, UUID serviceId);
}



