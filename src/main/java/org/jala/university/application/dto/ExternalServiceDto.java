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

    /**
     * Unique identifier for the external service.
     */
    UUID id;

    /**
     * Name of the service provider.
     */
    String providerName;

    /**
     * Account number or reference for the service provider.
     */
    String accountReference;

    /**
     * Phone country code.
     */
    String phoneCountryCode;

    /**
     * Phone number of the service provider contact.
     */
    String phoneNumber;

    /**
     * Email address of the service provider contact.
     */
    String email;

    /**
     * Additional contact details or notes.
     */
    String contactDetails;

    /**
     * Timestamp when the service was created.
     */
    LocalDateTime createdAt;

    /**
     * Timestamp when the service was last updated.
     */
    LocalDateTime updatedAt;

}

