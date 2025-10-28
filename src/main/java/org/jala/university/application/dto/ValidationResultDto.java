package org.jala.university.application.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * DTO representing the result of a validation operation.
 * Contains a boolean indicating success and a list of validation errors if any.
 */
@Value
@Builder
public class ValidationResultDto {
    boolean valid;
    List<ValidationErrorDto> errors;
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
}

