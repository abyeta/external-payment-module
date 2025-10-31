package org.jala.university.application.service;

import org.jala.university.application.dto.RegistrationDocumentDto;
import org.jala.university.application.dto.ValidationResultDto;
import org.jala.university.application.mapper.RegistrationDocumentMapper;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.entity.RegistrationDocument;
import org.jala.university.domain.repository.ExternalServiceRepository;
import org.jala.university.domain.repository.RegistrationDocumentRepository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class RegistrationDocumentServiceImpl implements RegistrationDocumentService{

    private final RegistrationDocumentRepository documentRepository;
    private final ExternalServiceRepository serviceRepository;
    private final RegistrationDocumentMapper mapper;


    public RegistrationDocumentServiceImpl(RegistrationDocumentRepository documentRepository, ExternalServiceRepository serviceRepository, RegistrationDocumentMapper mapper) {
        this.documentRepository = documentRepository;
        this.serviceRepository = serviceRepository;
        this.mapper = mapper;
    }


    @Override
    public void saveRegistrationDocuments(List<RegistrationDocumentDto> dtoList) {

        ExternalService service = serviceRepository.findById(dtoList.get(0).getExternalServiceId());

        for (RegistrationDocumentDto dto : dtoList) {
            try {
                RegistrationDocument registrationDocument = mapper.mapFrom(dto);
                registrationDocument.setExternalService(service);
                documentRepository.save(registrationDocument);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<RegistrationDocumentDto> findAllRegistrationDocuments(UUID externalServiceId) {
        return documentRepository.findAllById(externalServiceId).stream()
                .map(mapper::mapTo)
                .toList();
    }
}
