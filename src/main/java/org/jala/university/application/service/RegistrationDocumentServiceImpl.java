package org.jala.university.application.service;

import jakarta.persistence.EntityManagerFactory;
import org.jala.university.application.dto.RegistrationDocumentDto;
import org.jala.university.application.mapper.RegistrationDocumentMapper;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.entity.RegistrationDocument;
import org.jala.university.domain.repository.ExternalServiceRepository;
import org.jala.university.domain.repository.RegistrationDocumentRepository;
import org.jala.university.infrastructure.persistance.RegistrationDocumentRepositoryImpl;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of RegistrationDocumentService for managing registration documents.
 */
public class RegistrationDocumentServiceImpl implements RegistrationDocumentService {

    private final RegistrationDocumentRepository documentRepository;
    private final ExternalServiceRepository serviceRepository;
    private final RegistrationDocumentMapper mapper;

    /**
     * Constructor for RegistrationDocumentServiceImpl.
     *
     * @param documentRepository the document repository
     * @param serviceRepository  the service repository
     * @param mapper             the document mapper
     * @param emf
     */
    public RegistrationDocumentServiceImpl(RegistrationDocumentRepository documentRepository,
                                           ExternalServiceRepository serviceRepository,
                                           RegistrationDocumentMapper mapper, EntityManagerFactory emf) {
        this.documentRepository = documentRepository;
        this.serviceRepository = serviceRepository;
        this.mapper = mapper;
    }

    /**
     * Saves a list of registration documents.
     *
     * @param externalServiceId the ID of the external service
     * @param dtoList the list of document DTOs to save
     */
    @Override
    public void saveRegistrationDocuments(UUID externalServiceId, List<RegistrationDocumentDto> dtoList) {

        ExternalService service = serviceRepository.findById(externalServiceId);

        if (service == null) {
            throw new IllegalArgumentException("External service not found with id: " + externalServiceId);
        }

        for (RegistrationDocumentDto dto : dtoList) {
            RegistrationDocument registrationDocument = mapper.mapFrom(dto);
            registrationDocument.setExternalService(service);
            // Use implementation-specific saveWithoutTransaction when available
            if (documentRepository instanceof RegistrationDocumentRepositoryImpl) {
                RegistrationDocumentRepositoryImpl impl =
                        (RegistrationDocumentRepositoryImpl) documentRepository;
                impl.saveWithoutTransaction(registrationDocument);
            } else {
                documentRepository.save(registrationDocument);
            }
        }
    }

    /**
     * Finds all registration documents for a given external service ID.
     *
     * @param externalServiceId the external service ID
     * @return the list of document DTOs
     */
    @Override
    public List<RegistrationDocumentDto> findAllRegistrationDocuments(UUID externalServiceId) {
        return documentRepository.findAllById(externalServiceId).stream()
                .map(mapper::mapTo)
                .toList();
    }
}
