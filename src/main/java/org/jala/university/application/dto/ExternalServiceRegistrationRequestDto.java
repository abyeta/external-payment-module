package org.jala.university.application.dto;

import lombok.Builder;
import lombok.Value;

/**
 * DTO representing a request to register a new external service.
 * This is used to transfer data from the presentation layer to the service layer.
 */
@Value
@Builder
public class ExternalServiceRegistrationRequestDto {

    /**
     * Name of the service provider.
     * Must be alphanumeric with maximum 100 characters.
     */
    String providerName;

    /**
     * Account number or reference for the service provider.
     * Must be exactly 10 digits.
     */
    String accountReference;

    /**
     * Phone country code (e.g., +52, +1, +591).
     */
    String phoneCountryCode;

    /**
     * Phone number of the service provider contact.
     * Must be exactly 10 digits.
     */
    String phoneNumber;

    /**
     * Email address of the service provider contact.
     */
    String email;

    /**
     * Additional contact details or notes.
     * This field is optional.
     */
    String contactDetails;
}

