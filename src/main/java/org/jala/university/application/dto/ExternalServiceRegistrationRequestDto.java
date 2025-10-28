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
    String providerName;
    String accountReference;
    String phoneCountryCode;
    String phoneNumber;
    String email;
    String contactDetails;
}

