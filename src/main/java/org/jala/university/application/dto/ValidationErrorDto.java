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
    String field;
    String message;
    String errorCode;
}

