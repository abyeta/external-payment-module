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

    /**
     * Indicates whether the validation passed (true) or failed (false).
     */
    boolean valid;

    /**
     * List of validation errors. Empty if validation passed.
     */
    List<ValidationErrorDto> errors;

    /**
     * Checks if there are any validation errors.
     *
     * @return true if there are errors, false otherwise
     */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
}

