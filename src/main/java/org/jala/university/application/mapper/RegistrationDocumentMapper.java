package org.jala.university.application.mapper;

import org.jala.university.application.dto.RegistrationDocumentDto;
import org.jala.university.commons.application.mapper.Mapper;
import org.jala.university.domain.entity.RegistrationDocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RegistrationDocumentMapper implements Mapper<RegistrationDocument, RegistrationDocumentDto> {
    @Override
    public RegistrationDocumentDto mapTo(RegistrationDocument registrationDocument) {
        if (registrationDocument == null) {
            return null;
        }

        // TODO : Need implement file mapping
        return RegistrationDocumentDto.builder()
                .id(registrationDocument.getId())
                .fileName(registrationDocument.getFileName())
                .createdAt(registrationDocument.getCreatedAt())
                .updatedAt(registrationDocument.getUpdatedAt())
                .build();
    }

    @Override
    public RegistrationDocument mapFrom(RegistrationDocumentDto registrationDocumentDto) throws IOException {
        if (registrationDocumentDto == null) {
            return null;
        }

        return RegistrationDocument.builder()
                .id(registrationDocumentDto.getId())
                .file(Files.readAllBytes(registrationDocumentDto.getFile().toPath()))
                .fileName(registrationDocumentDto.getFileName())
                .createdAt(registrationDocumentDto.getCreatedAt())
                .updatedAt(registrationDocumentDto.getUpdatedAt())
                .build();
    }
}
