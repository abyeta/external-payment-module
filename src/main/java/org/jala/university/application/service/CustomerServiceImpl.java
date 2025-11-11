package org.jala.university.application.service;

import lombok.RequiredArgsConstructor;
import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.application.mapper.ExternalServiceMapper;
import org.jala.university.domain.repository.CustomerRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of CustomerServiceLinkService.
 * Handles business logic for searching and linking external services to customers.
 * This class is final and not designed for extension.
 */
@RequiredArgsConstructor
public final class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ExternalServiceMapper mapper;

    @Override
    public List<ExternalServiceDto> getLinkedServices(UUID customerId) {
        return customerRepository.getLinkedServices(customerId).stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    public void linkService(UUID customerId, UUID serviceId) {
        customerRepository.linkService(customerId, serviceId);
    }

    @Override
    public void unlinkService(UUID customerId, UUID serviceId) {
        customerRepository.unlinkService(customerId, serviceId);
    }

    @Override
    public boolean isServiceLinked(UUID customerId, UUID serviceId) {
       return customerRepository.isServiceLinked(customerId, serviceId);
    }
}
