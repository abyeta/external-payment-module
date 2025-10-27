package org.jala.university.application.service;

import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.application.dto.ExternalServiceRegistrationRequestDto;
import org.jala.university.application.dto.ValidationResultDto;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for external service registration operations.
 * Handles business logic for registering and managing external service providers.
 */
public interface ExternalServiceRegistrationService {

    /**
     * Validates external service registration data without saving.
     *
     * @param request the registration request to validate
     * @return ValidationResultDto with validation results
     */
    ValidationResultDto validateServiceData(ExternalServiceRegistrationRequestDto request);

    /**
     * Submits an external service registration for approval.
     * Changes status to PENDING_APPROVAL.
     *
     * @param request the registration request
     * @return the submitted external service DTO
     * @throws IllegalArgumentException if validation fails
     */
    ExternalServiceDto submitRegistration(ExternalServiceRegistrationRequestDto request);

    /**
     * Finds an external service by ID.
     *
     * @param id the ID to search for
     * @return the external service DTO if found
     * @throws IllegalStateException if service doesn't exist
     */
    ExternalServiceDto findById(UUID id);

    /**
     * Retrieves all external services.
     *
     * @return list of all external service DTOs
     */
    List<ExternalServiceDto> findAll();

    /**
     * Deletes an external service by ID.
     *
     * @param id the ID of the service to delete
     * @throws IllegalStateException if service doesn't exist
     */
    void delete(UUID id);
}

