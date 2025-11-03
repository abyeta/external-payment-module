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

    Optional<ExternalService> findByAccountReference(String accountReference);

    boolean existsByAccountReference(String accountReference);

    boolean existsByProviderName(String providerName);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    ExternalService saveAndFlush(ExternalService entity);
}



