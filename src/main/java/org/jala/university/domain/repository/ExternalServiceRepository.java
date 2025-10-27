package org.jala.university.domain.repository;

import org.jala.university.commons.domain.repository.Repository;
import org.jala.university.domain.entity.ExternalService;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for ExternalService entity operations.
 * Extends the base Repository interface and adds specific query methods
 * for external service management.
 */
public interface ExternalServiceRepository extends Repository<ExternalService, UUID> {

    /**
     * Finds an external service by its account reference number.
     *
     * @param accountReference the account reference to search for
     * @return Optional containing the external service if found, empty otherwise
     */
    Optional<ExternalService> findByAccountReference(String accountReference);

    /**
     * Checks if an external service with the given account reference exists.
     *
     * @param accountReference the account reference to check
     * @return true if exists, false otherwise
     */
    boolean existsByAccountReference(String accountReference);
}

