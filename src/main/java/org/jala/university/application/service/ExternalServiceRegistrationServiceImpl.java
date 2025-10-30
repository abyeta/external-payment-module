package org.jala.university.application.service;

import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.application.dto.ExternalServiceRegistrationRequestDto;
import org.jala.university.application.dto.ValidationResultDto;
import org.jala.university.application.mapper.ExternalServiceMapper;
import org.jala.university.application.validator.ServiceDataValidator;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.repository.ExternalServiceRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of ExternalServiceRegistrationService.
 * Handles business logic for external service registration operations.
 */
public final class ExternalServiceRegistrationServiceImpl implements ExternalServiceRegistrationService {

    private static final String DEFAULT_USER = "admin";

    private final ExternalServiceRepository repository;
    private final ExternalServiceMapper mapper;
    private final ServiceDataValidator validator;

    public ExternalServiceRegistrationServiceImpl(ExternalServiceRepository repository,
                                                   ExternalServiceMapper mapper,
                                                   ServiceDataValidator validator) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
    }

    @Override
    public ValidationResultDto validateServiceData(ExternalServiceRegistrationRequestDto request) {
        return validator.validateAll(request);
    }

    @Override
    public ExternalServiceDto submitRegistration(ExternalServiceRegistrationRequestDto request) {
        // Validate data
        ValidationResultDto validationResult = validator.validateAll(request);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Validation failed: " + validationResult.getErrors());
        }

        // Check if account reference already exists
        if (repository.existsByAccountReference(request.getAccountReference())) {
            throw new IllegalArgumentException("Account reference already exists");
        }

        ExternalService entity = mapper.mapFromRequest(request);

        ExternalService savedEntity = repository.save(entity);

        return mapper.mapTo(savedEntity);
    }

    @Override
    public ExternalServiceDto findById(UUID id) {
        ExternalService entity = repository.findById(id);
        if (entity == null) {
            throw new IllegalStateException("External service not found with id: " + id);
        }
        return mapper.mapTo(entity);
    }

    @Override
    public List<ExternalServiceDto> findAll() {
        List<ExternalService> entities = repository.findAll();
        return entities.stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        ExternalService entity = repository.findById(id);
        if (entity == null) {
            throw new IllegalStateException("External service not found with id: " + id);
        }
        repository.deleteById(id);
    }
}



