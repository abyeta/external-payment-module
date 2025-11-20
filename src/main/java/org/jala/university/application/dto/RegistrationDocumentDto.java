package org.jala.university.application.dto;

import lombok.Builder;
import lombok.Value;

import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class RegistrationDocumentDto {

    UUID id;
    String fileName;
    File file;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    UUID externalServiceId;

}
