package org.jala.university.application.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO representing a complete external service entity.
 * This is used to transfer data from the service layer to the presentation layer.
 */
@Value
@Builder
public class ExternalServiceDto {


    UUID id;
    String providerName;
    String accountReference;
    String phoneCountryCode;
    String phoneNumber;
    String email;
    String contactDetails;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

}

