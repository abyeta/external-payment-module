package org.jala.university.application.mapper;

import org.jala.university.application.dto.RegistrationDocumentDto;
import org.jala.university.commons.application.mapper.Mapper;
import org.jala.university.domain.entity.RegistrationDocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Mapper for converting between RegistrationDocument entities and DTOs.
 */
public final class RegistrationDocumentMapper implements Mapper<RegistrationDocument, RegistrationDocumentDto> {

    @Override
    public RegistrationDocumentDto mapTo(RegistrationDocument registrationDocument) {
        if (registrationDocument == null) {
            return null;
        }

        // Convertir byte[] a File temporal para el DTO
        File tempFile = null;
        try {
            if (registrationDocument.getFile() != null && registrationDocument.getFile().length > 0) {
                tempFile = File.createTempFile("doc_", "_" + registrationDocument.getFileName());
                Files.write(tempFile.toPath(), registrationDocument.getFile());
                tempFile.deleteOnExit();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creating temp file for: " + registrationDocument.getFileName(), e);
        }

        return RegistrationDocumentDto.builder()
                .id(registrationDocument.getId())
                .fileName(registrationDocument.getFileName())
                .file(tempFile)  // para mappear el archivo correctamente
                .createdAt(registrationDocument.getCreatedAt())
                .updatedAt(registrationDocument.getUpdatedAt())
                .externalServiceId(registrationDocument.getExternalService() != null
                        ? registrationDocument.getExternalService().getId()
                        : null)
                .build();
    }

    @Override
    public RegistrationDocument mapFrom(RegistrationDocumentDto registrationDocumentDto) {
        if (registrationDocumentDto == null) {
            return null;
        }

        try {
            return RegistrationDocument.builder()
                    .id(registrationDocumentDto.getId())
                    .file(Files.readAllBytes(registrationDocumentDto.getFile().toPath()))
                    .fileName(registrationDocumentDto.getFileName())
                    .createdAt(registrationDocumentDto.getCreatedAt())
                    .updatedAt(registrationDocumentDto.getUpdatedAt())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo: " + registrationDocumentDto.getFileName(), e);
        }
    }
}
