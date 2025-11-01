package org.jala.university.application.validator;

import org.jala.university.application.dto.ExternalServiceRegistrationRequestDto;
import org.jala.university.application.dto.ValidationErrorDto;
import org.jala.university.application.dto.ValidationResultDto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Validator for external service registration data.
 * Validates all fields according to business rules and returns detailed error information.
 */
public final class ServiceDataValidator {

    private static final int BYTES_PER_KB = 1024;
    private static final int BYTES_PER_MB = 1024;

    public ValidationErrorDto validateProviderName(String providerName) {
        if (providerName == null || providerName.trim().isEmpty()) {
            return ValidationErrorDto.builder()
                    .field(ValidationConstants.FIELD_PROVIDER_NAME)
                    .message(ValidationConstants.MSG_PROVIDER_NAME_REQUIRED)
                    .errorCode(ValidationConstants.ERROR_REQUIRED)
                    .build();
        }

        if (providerName.length() > ValidationConstants.PROVIDER_NAME_MAX_LENGTH) {
            return ValidationErrorDto.builder()
                    .field(ValidationConstants.FIELD_PROVIDER_NAME)
                    .message(ValidationConstants.MSG_PROVIDER_NAME_TOO_LONG)
                    .errorCode(ValidationConstants.ERROR_INVALID_LENGTH)
                    .build();
        }

        if (!ValidationConstants.PROVIDER_NAME_PATTERN.matcher(providerName).matches()) {
            return ValidationErrorDto.builder()
                    .field(ValidationConstants.FIELD_PROVIDER_NAME)
                    .message(ValidationConstants.MSG_PROVIDER_NAME_INVALID)
                    .errorCode(ValidationConstants.ERROR_ALPHANUMERIC_ONLY)
                    .build();
        }

        return null;
    }

    public ValidationErrorDto validateAccountReference(String accountReference) {
        if (accountReference == null || accountReference.trim().isEmpty()) {
            return ValidationErrorDto.builder()
                    .field(ValidationConstants.FIELD_ACCOUNT_REFERENCE)
                    .message(ValidationConstants.MSG_ACCOUNT_REFERENCE_REQUIRED)
                    .errorCode(ValidationConstants.ERROR_REQUIRED)
                    .build();
        }

        if (!ValidationConstants.ACCOUNT_REFERENCE_PATTERN.matcher(accountReference).matches()) {
            return ValidationErrorDto.builder()
                    .field(ValidationConstants.FIELD_ACCOUNT_REFERENCE)
                    .message(ValidationConstants.MSG_ACCOUNT_REFERENCE_INVALID)
                    .errorCode(ValidationConstants.ERROR_INVALID_FORMAT)
                    .build();
        }

        return null;
    }

    public ValidationErrorDto validatePhoneCountryCode(String phoneCountryCode) {
        if (phoneCountryCode == null || phoneCountryCode.trim().isEmpty()) {
            return ValidationErrorDto.builder()
                    .field(ValidationConstants.FIELD_PHONE_COUNTRY_CODE)
                    .message(ValidationConstants.MSG_PHONE_COUNTRY_CODE_REQUIRED)
                    .errorCode(ValidationConstants.ERROR_REQUIRED)
                    .build();
        }

        if (!ValidationConstants.PHONE_COUNTRY_CODE_PATTERN.matcher(phoneCountryCode).matches()) {
            return ValidationErrorDto.builder()
                    .field(ValidationConstants.FIELD_PHONE_COUNTRY_CODE)
                    .message(ValidationConstants.MSG_PHONE_COUNTRY_CODE_INVALID)
                    .errorCode(ValidationConstants.ERROR_INVALID_FORMAT)
                    .build();
        }

        return null;
    }

    public ValidationErrorDto validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return ValidationErrorDto.builder()
                    .field(ValidationConstants.FIELD_PHONE_NUMBER)
                    .message(ValidationConstants.MSG_PHONE_NUMBER_REQUIRED)
                    .errorCode(ValidationConstants.ERROR_REQUIRED)
                    .build();
        }

        if (!ValidationConstants.PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
            return ValidationErrorDto.builder()
                    .field(ValidationConstants.FIELD_PHONE_NUMBER)
                    .message(ValidationConstants.MSG_PHONE_NUMBER_INVALID)
                    .errorCode(ValidationConstants.ERROR_INVALID_FORMAT)
                    .build();
        }

        return null;
    }

    public ValidationErrorDto validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return ValidationErrorDto.builder()
                    .field(ValidationConstants.FIELD_EMAIL)
                    .message(ValidationConstants.MSG_EMAIL_REQUIRED)
                    .errorCode(ValidationConstants.ERROR_REQUIRED)
                    .build();
        }

        if (!ValidationConstants.EMAIL_PATTERN.matcher(email).matches()) {
            return ValidationErrorDto.builder()
                    .field(ValidationConstants.FIELD_EMAIL)
                    .message(ValidationConstants.MSG_EMAIL_INVALID)
                    .errorCode(ValidationConstants.ERROR_INVALID_FORMAT)
                    .build();
        }

        return null;
    }

    public ValidationErrorDto validateExistingFiles(List<File> files) {
        if (files == null || files.isEmpty()) {
            return ValidationErrorDto.builder()
                    .field(ValidationConstants.FIELD_FILES)
                    .errorCode(ValidationConstants.ERROR_REQUIRED)
                    .message(ValidationConstants.MSG_FILES_REQUIRED)
                    .build();
        }

        return null;
    }

    public ValidationErrorDto validateFileSize(File file) {

        long fileSize = file.length() / (BYTES_PER_KB * BYTES_PER_MB);

        if (fileSize > ValidationConstants.MAX_FILE_SIZE) {
            return ValidationErrorDto.builder()
                    .field(ValidationConstants.FIELD_FILES)
                    .errorCode(ValidationConstants.ERROR_FILE_SIZE_EXCEEDED)
                    .message(ValidationConstants.MSG_FILE_SIZE_INVALID + file.getName())
                    .build();
        }

        return null;
    }

    public ValidationResultDto validateAll(ExternalServiceRegistrationRequestDto requestDto) {
        List<ValidationErrorDto> errors = new ArrayList<>();

        if (requestDto == null) {
            return ValidationResultDto.builder()
                    .valid(false)
                    .errors(errors)
                    .build();
        }

        // Validate provider name
        ValidationErrorDto providerNameError = validateProviderName(requestDto.getProviderName());
        if (providerNameError != null) {
            errors.add(providerNameError);
        }

        // Validate account reference
        ValidationErrorDto accountRefError = validateAccountReference(requestDto.getAccountReference());
        if (accountRefError != null) {
            errors.add(accountRefError);
        }

        // Validate phone country code
        ValidationErrorDto phoneCountryCodeError = validatePhoneCountryCode(requestDto.getPhoneCountryCode());
        if (phoneCountryCodeError != null) {
            errors.add(phoneCountryCodeError);
        }

        // Validate phone number
        ValidationErrorDto phoneNumberError = validatePhoneNumber(requestDto.getPhoneNumber());
        if (phoneNumberError != null) {
            errors.add(phoneNumberError);
        }

        // Validate email
        ValidationErrorDto emailError = validateEmail(requestDto.getEmail());
        if (emailError != null) {
            errors.add(emailError);
        }

        //Validate files integrity
        ValidationErrorDto filesError = validateExistingFiles(requestDto.getFiles());
        if (filesError != null) {
            errors.add(filesError);
        } else {
            for (File file : requestDto.getFiles()) {
                ValidationErrorDto fileError = validateFileSize(file);
                if(fileError != null){
                    errors.add(fileError);
                }
            }
        }

        // contactDetails is optional, no validation needed

        return ValidationResultDto.builder()
                .valid(errors.isEmpty())
                .errors(errors)
                .build();
    }
}



