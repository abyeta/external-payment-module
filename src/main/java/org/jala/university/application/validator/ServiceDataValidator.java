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

    /**
     * Valida todos los campos del request (alias de validateForRegistration para compatibilidad).
     *
     * @param requestDto DTO con los datos a validar.
     * @return Resultado de la validación.
     */
    public ValidationResultDto validateAll(ExternalServiceRegistrationRequestDto requestDto) {
        return validateForRegistration(requestDto);
    }

    /**
     * Valida el nombre del proveedor.
     *
     * @param providerName Nombre del proveedor.
     * @return Error de validación si es inválido, o null si es válido.
     */
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

    /**
     * Valida la referencia de cuenta.
     *
     * @param accountReference Referencia de cuenta.
     * @return Error de validación si es inválida, o null si es válida.
     */
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

    /**
     * Valida el código de país del teléfono.
     *
     * @param phoneCountryCode Código de país.
     * @return Error de validación si es inválido, o null si es válido.
     */
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

    /**
     * Valida el número de teléfono.
     *
     * @param phoneNumber Número de teléfono.
     * @return Error de validación si es inválido, o null si es válido.
     */
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

    /**
     * Valida el correo electrónico.
     *
     * @param email Dirección de correo electrónico.
     * @return Error de validación si es inválido, o null si es válido.
     */
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

    /**
     * Valida que haya al menos un archivo (solo para registro).
     * En actualización, se maneja por separado.
     *
     * @param files Lista de archivos.
     * @return Error si no hay archivos, o null si es válido.
     */
    public ValidationErrorDto validateFilesForRegistration(List<File> files) {
        if (files == null || files.isEmpty()) {
            return ValidationErrorDto.builder()
                    .field(ValidationConstants.FIELD_FILES)
                    .errorCode(ValidationConstants.ERROR_REQUIRED)
                    .message(ValidationConstants.MSG_FILES_REQUIRED)
                    .build();
        }

        return null;
    }

    /**
     * Valida el tamaño de un archivo.
     *
     * @param file Archivo a validar.
     * @return Error si excede el tamaño permitido, o null si es válido.
     */
    public ValidationErrorDto validateFileSize(File file) {
        long fileSizeInMb = file.length() / (BYTES_PER_KB * BYTES_PER_MB);

        if (fileSizeInMb > ValidationConstants.MAX_FILE_SIZE) {
            return ValidationErrorDto.builder()
                    .field(ValidationConstants.FIELD_FILES)
                    .errorCode(ValidationConstants.ERROR_FILE_SIZE_EXCEEDED)
                    .message(ValidationConstants.MSG_FILE_SIZE_INVALID + file.getName())
                    .build();
        }

        return null;
    }

    /**
     * Valida todos los campos para REGISTRO.
     * NO valida archivos aquí → se hace en el controlador.
     *
     * @param requestDto DTO con los datos a validar.
     * @return Resultado de la validación.
     */
    public ValidationResultDto validateForRegistration(ExternalServiceRegistrationRequestDto requestDto) {
        List<ValidationErrorDto> errors = new ArrayList<>();

        if (requestDto == null) {
            return ValidationResultDto.builder().valid(false).errors(errors).build();
        }

        addError(errors, validateProviderName(requestDto.getProviderName()));
        addError(errors, validateAccountReference(requestDto.getAccountReference()));
        addError(errors, validatePhoneCountryCode(requestDto.getPhoneCountryCode()));
        addError(errors, validatePhoneNumber(requestDto.getPhoneNumber()));
        addError(errors, validateEmail(requestDto.getEmail()));

        List<File> files = requestDto.getFiles();
        if (files != null && !files.isEmpty()) {
            ValidationErrorDto filesError = validateFilesForRegistration(files);
            addError(errors, filesError);

            for (File file : files) {
                addError(errors, validateFileSize(file));
            }
        }

        return ValidationResultDto.builder()
                .valid(errors.isEmpty())
                .errors(errors)
                .build();
    }

    /**
     * Valida solo campos de texto para ACTUALIZACIÓN.
     * NO toca archivos.
     *
     * @param requestDto DTO con los datos a validar.
     * @return Resultado de la validación.
     */
    public ValidationResultDto validateForUpdate(ExternalServiceRegistrationRequestDto requestDto) {
        List<ValidationErrorDto> errors = new ArrayList<>();

        if (requestDto == null) {
            return ValidationResultDto.builder().valid(false).errors(errors).build();
        }

        addError(errors, validateProviderName(requestDto.getProviderName()));
        addError(errors, validateAccountReference(requestDto.getAccountReference()));
        addError(errors, validatePhoneCountryCode(requestDto.getPhoneCountryCode()));
        addError(errors, validatePhoneNumber(requestDto.getPhoneNumber()));
        addError(errors, validateEmail(requestDto.getEmail()));

        return ValidationResultDto.builder()
                .valid(errors.isEmpty())
                .errors(errors)
                .build();
    }

    /**
     * Agrega un error a la lista si no es null.
     *
     * @param errors Lista de errores acumulados.
     * @param error  Error individual a agregar.
     */
    private void addError(List<ValidationErrorDto> errors, ValidationErrorDto error) {
        if (error != null) {
            errors.add(error);
        }
    }
}
