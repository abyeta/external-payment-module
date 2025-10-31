package org.jala.university.application.service;

import org.jala.university.application.dto.RegistrationDocumentDto;

import java.util.List;
import java.util.UUID;

public interface RegistrationDocumentService {

    void saveRegistrationDocuments(List<RegistrationDocumentDto> registrationDocumentDtoList);

    List<RegistrationDocumentDto> findAllRegistrationDocuments(UUID externalServiceId);

}
