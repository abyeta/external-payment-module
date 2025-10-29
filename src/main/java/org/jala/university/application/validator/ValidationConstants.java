package org.jala.university.application.validator;

import java.util.regex.Pattern;

/**
 * Constants used for validation of external service registration data.
 * Contains regex patterns, length limits, and error messages.
 */
public final class ValidationConstants {

    // Field names
    public static final String FIELD_PROVIDER_NAME = "providerName";
    public static final String FIELD_ACCOUNT_REFERENCE = "accountReference";
    public static final String FIELD_PHONE_COUNTRY_CODE = "phoneCountryCode";
    public static final String FIELD_PHONE_NUMBER = "phoneNumber";
    public static final String FIELD_EMAIL = "email";

    // Length constraints
    public static final int PROVIDER_NAME_MAX_LENGTH = 100;
    public static final int ACCOUNT_REFERENCE_LENGTH = 10;
    public static final int PHONE_NUMBER_LENGTH = 10;
    public static final int PHONE_COUNTRY_CODE_MAX_LENGTH = 5;
    public static final int CREATED_BY_MAX_LENGTH = 100;

    // Regex patterns
    public static final Pattern PROVIDER_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s]{1,100}$");
    public static final Pattern ACCOUNT_REFERENCE_PATTERN = Pattern.compile("^\\d{10}$");
    public static final Pattern PHONE_COUNTRY_CODE_PATTERN = Pattern.compile("^\\+\\d{1,4}$");
    public static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^\\d{10}$");
    public static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    // Error codes
    public static final String ERROR_REQUIRED = "REQUIRED";
    public static final String ERROR_INVALID_FORMAT = "INVALID_FORMAT";
    public static final String ERROR_INVALID_LENGTH = "INVALID_LENGTH";
    public static final String ERROR_ALPHANUMERIC_ONLY = "ALPHANUMERIC_ONLY";
    public static final String ERROR_NUMERIC_ONLY = "NUMERIC_ONLY";
    public static final String ERROR_DUPLICATE = "DUPLICATE";

    // Error messages
    public static final String MSG_PROVIDER_NAME_REQUIRED = "El nombre del proveedor es requerido";
    public static final String MSG_PROVIDER_NAME_INVALID =
            "El nombre solo puede contener letras, números y espacios";
    public static final String MSG_PROVIDER_NAME_TOO_LONG =
            "El nombre no puede exceder 100 caracteres";

    public static final String MSG_ACCOUNT_REFERENCE_REQUIRED = "El número de cuenta es requerido";
    public static final String MSG_ACCOUNT_REFERENCE_INVALID =
            "El número de cuenta debe contener exactamente 10 dígitos";
    public static final String MSG_ACCOUNT_REFERENCE_DUPLICATE =
            "Este número de cuenta ya está registrado";

    public static final String MSG_PHONE_COUNTRY_CODE_REQUIRED = "El código de país es requerido";
    public static final String MSG_PHONE_COUNTRY_CODE_INVALID =
            "Formato inválido de código de país (ejemplo: +591)";

    public static final String MSG_PHONE_NUMBER_REQUIRED = "El número de teléfono es requerido";
    public static final String MSG_PHONE_NUMBER_INVALID =
            "El número de teléfono debe contener exactamente 10 dígitos";

    public static final String MSG_EMAIL_REQUIRED = "El correo electrónico es requerido";
    public static final String MSG_EMAIL_INVALID = "Formato de correo electrónico inválido";

    /**
     * Private constructor to prevent instantiation.
     */
    private ValidationConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}



