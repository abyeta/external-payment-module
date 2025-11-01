package org.jala.university.application.service;

import org.jala.university.application.dto.RegistrationDocumentDto;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for RegistrationDocument operations.
 */
public interface RegistrationDocumentService {

    /**
     * Saves registration documents for a specific service.
     *
     * @param externalServiceId the ID of the external service
     * @param registrationDocumentDtoList the list of documents to save
     */
    void saveRegistrationDocuments(UUID externalServiceId,
                                    List<RegistrationDocumentDto> registrationDocumentDtoList);

    /**
     * Finds all registration documents for a given external service.
     *
     * @param externalServiceId the external service ID
     * @return list of registration documents
     */
    List<RegistrationDocumentDto> findAllRegistrationDocuments(UUID externalServiceId);

}
