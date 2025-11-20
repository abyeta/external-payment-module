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

    ValidationResultDto validateServiceData(ExternalServiceRegistrationRequestDto request);

    ExternalServiceDto submitRegistration(ExternalServiceRegistrationRequestDto request);

    ExternalServiceDto findById(UUID id);

    List<ExternalServiceDto> findAll();

    void delete(UUID id);

    ExternalServiceDto setEnabled(UUID id, boolean enabled);

    List<ExternalServiceDto> searchServices(String searchTerm);


}



