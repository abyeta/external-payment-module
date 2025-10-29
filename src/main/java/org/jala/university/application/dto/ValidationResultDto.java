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

    /**
     * Checks if there are any validation errors.
     * This method is safe to override in subclasses if needed.
     *
     * @return true if there are errors, false otherwise
     */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
}
