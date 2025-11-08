package org.jala.university.application.service;

import org.jala.university.application.dto.ExternalServiceRegistrationRequestDto;
import org.jala.university.application.dto.ExternalServiceDto;
import java.util.List;
import java.util.UUID;

public interface ExternalServiceUpdateService {

    ExternalServiceDto update(UUID id, ExternalServiceRegistrationRequestDto request);

    ExternalServiceDto findById(UUID id);
    List<ExternalServiceDto> findAll();

    void deleteByIds(List<UUID> docIds);
}
