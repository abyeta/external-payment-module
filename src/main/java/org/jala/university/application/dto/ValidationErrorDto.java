package org.jala.university.application.dto;

import lombok.Builder;
import lombok.Value;

/**
 * DTO representing a validation error for a specific field.
 * This is used to communicate validation failures to the presentation layer.
 */
@Value
@Builder
public class ValidationErrorDto {

    /**
     * The name of the field that failed validation.
     */
    String field;

    /**
     * Human-readable error message describing the validation failure.
     */
    String message;

    /**
     * Error code for programmatic handling of errors.
     */
    String errorCode;
}

