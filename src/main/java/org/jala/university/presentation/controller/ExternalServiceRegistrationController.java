package org.jala.university.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.application.dto.ExternalServiceRegistrationRequestDto;
import org.jala.university.application.dto.ValidationResultDto;
import org.jala.university.application.mapper.ExternalServiceMapper;
import org.jala.university.application.service.ExternalServiceRegistrationService;
import org.jala.university.application.validator.ServiceDataValidator;
import org.jala.university.commons.presentation.BaseController;
import org.jala.university.commons.presentation.ViewSwitcher;
import org.jala.university.domain.repository.ExternalServiceRepository;
import org.jala.university.infrastructure.persistance.ExternalServiceRepositoryImpl;
import org.jala.university.presentation.ExternalPaymentView;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

/**
 * Controller for the External Service Registration view.
 * Handles user interactions, validation, and communication with the service layer.
 */
public class ExternalServiceRegistrationController extends BaseController {

    private static final int MAX_PROVIDER_NAME_LENGTH = 100;
    private static final int ACCOUNT_REFERENCE_LENGTH = 10;
    private static final int PHONE_NUMBER_LENGTH = 10;

    // FXML Components - Header
    @FXML
    private Label pageTitleLabel;

    @FXML
    private Label pageDescriptionLabel;

    // FXML Components - Form Fields
    @FXML
    private TextField providerNameField;

    @FXML
    private TextField accountReferenceField;

    @FXML
    private ComboBox<String> phoneCountryCodeComboBox;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField emailField;

    @FXML
    private TextArea contactDetailsArea;

    // FXML Components - Labels
    @FXML
    private Label providerNameError;

    @FXML
    private Label accountReferenceError;

    @FXML
    private Label phoneNumberError;

    @FXML
    private Label emailError;

    // FXML Components - Buttons
    @FXML
    private Button cancelButton;

    @FXML
    private Button submitButton;

    // FXML Components - Feedback
    @FXML
    private Label feedbackLabel;

    // Services and dependencies
    private ExternalServiceRegistrationService service;
    private ServiceDataValidator validator;

    /**
     * Initializes the controller. This method is called automatically by JavaFX
     * after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        initializeServices();
        setupTextFormatters();
        populateCountryCodeComboBox();
        setupFieldValidators();
        disableSubmitButton();
    }

    /**
     * Initializes services and dependencies.
     */
    private void initializeServices() {
        // Initialize validator
        validator = new ServiceDataValidator();

        // Initialize EntityManager and repository
        EntityManager entityManager = Persistence
                .createEntityManagerFactory("external-payment-pu")
                .createEntityManager();
        ExternalServiceRepository repository = new ExternalServiceRepositoryImpl(entityManager);

        // Initialize mapper and service
        ExternalServiceMapper mapper = new ExternalServiceMapper();
        service = new org.jala.university.application.service.ExternalServiceRegistrationServiceImpl(
                repository, mapper, validator);
    }

    /**
     * Sets up text formatters for fields that require specific formatting.
     */
    private void setupTextFormatters() {
        // Provider name: max 100 characters
        providerNameField.setTextFormatter(createMaxLengthFormatter(MAX_PROVIDER_NAME_LENGTH));

        // Account reference: only digits, max 10
        accountReferenceField.setTextFormatter(createNumericFormatter(ACCOUNT_REFERENCE_LENGTH));

        // Phone number: only digits, max 10
        phoneNumberField.setTextFormatter(createNumericFormatter(PHONE_NUMBER_LENGTH));
    }

    /**
     * Creates a TextFormatter that allows only numeric input up to a specified length.
     *
     * @param maxLength the maximum length allowed
     * @return the configured TextFormatter
     */
    private TextFormatter<String> createNumericFormatter(int maxLength) {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*") && newText.length() <= maxLength) {
                return change;
            }
            return null;
        });
    }

    /**
     * Creates a TextFormatter that limits text length.
     *
     * @param maxLength the maximum length allowed
     * @return the configured TextFormatter
     */
    private TextFormatter<String> createMaxLengthFormatter(int maxLength) {
        return new TextFormatter<>(change -> {
            if (change.getControlNewText().length() <= maxLength) {
                return change;
            }
            return null;
        });
    }

    /**
     * Populates the country code ComboBox with common country codes.
     */
    private void populateCountryCodeComboBox() {
        phoneCountryCodeComboBox.getItems().addAll(
                "+591", // Bolivia
                "+52",  // México
                "+1",   // USA/Canadá
                "+51",  // Perú
                "+56",  // Chile
                "+54",  // Argentina
                "+55",  // Brasil
                "+57",  // Colombia
                "+34",  // España
                "+44"   // Reino Unido
        );
        phoneCountryCodeComboBox.setValue("+591"); // Default
    }

    /**
     * Sets up real-time validators for all form fields.
     */
    private void setupFieldValidators() {
        // Provider name validation
        providerNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateProviderNameField();
            updateSubmitButtonState();
        });

        // Account reference validation
        accountReferenceField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateAccountReferenceField();
            updateSubmitButtonState();
        });

        // Phone number validation
        phoneNumberField.textProperty().addListener((obs, oldVal, newVal) -> {
            validatePhoneNumberField();
            updateSubmitButtonState();
        });

        // Email validation
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateEmailField();
            updateSubmitButtonState();
        });

        // Country code validation
        phoneCountryCodeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            validatePhoneNumberField();
            updateSubmitButtonState();
        });
    }

    /**
     * Validates the provider name field and shows/hides error message.
     *
     * @return true if valid, false otherwise
     */
    private boolean validateProviderNameField() {
        String value = providerNameField.getText();
        var error = validator.validateProviderName(value);

        if (error != null) {
            showFieldError(providerNameField, providerNameError, error.getMessage());
            return false;
        } else {
            hideFieldError(providerNameField, providerNameError);
            return true;
        }
    }

    /**
     * Validates the account reference field and shows/hides error message.
     *
     * @return true if valid, false otherwise
     */
    private boolean validateAccountReferenceField() {
        String value = accountReferenceField.getText();
        var error = validator.validateAccountReference(value);

        if (error != null) {
            showFieldError(accountReferenceField, accountReferenceError, error.getMessage());
            return false;
        } else {
            hideFieldError(accountReferenceField, accountReferenceError);
            return true;
        }
    }

    /**
     * Validates the phone number field and shows/hides error message.
     *
     * @return true if valid, false otherwise
     */
    private boolean validatePhoneNumberField() {
        String countryCode = phoneCountryCodeComboBox.getValue();
        String number = phoneNumberField.getText();

        var countryCodeError = validator.validatePhoneCountryCode(countryCode);
        var numberError = validator.validatePhoneNumber(number);

        if (countryCodeError != null || numberError != null) {
            String message = countryCodeError != null
                    ? countryCodeError.getMessage()
                    : numberError.getMessage();
            showFieldError(phoneNumberField, phoneNumberError, message);
            return false;
        } else {
            hideFieldError(phoneNumberField, phoneNumberError);
            return true;
        }
    }

    /**
     * Validates the email field and shows/hides error message.
     *
     * @return true if valid, false otherwise
     */
    private boolean validateEmailField() {
        String value = emailField.getText();
        var error = validator.validateEmail(value);

        if (error != null) {
            showFieldError(emailField, emailError, error.getMessage());
            return false;
        } else {
            hideFieldError(emailField, emailError);
            return true;
        }
    }

    /**
     * Checks if the entire form is valid.
     *
     * @return true if all fields are valid, false otherwise
     */
    private boolean isFormValid() {
        return validateProviderNameField()
                && validateAccountReferenceField()
                && validatePhoneNumberField()
                && validateEmailField();
    }

    /**
     * Updates the submit button enabled/disabled state based on form validity.
     */
    private void updateSubmitButtonState() {
        submitButton.setDisable(!isFormValid());
    }

    /**
     * Disables the submit button initially.
     */
    private void disableSubmitButton() {
        submitButton.setDisable(true);
    }

    /**
     * Handler for the Back to Main button.
     * Navigates back to the main menu view.
     */
    @FXML
    private void onBackToMain() {
        ViewSwitcher.switchTo(ExternalPaymentView.MAIN.getView());
    }

    /**
     * Handler for the Cancel button.
     */
    @FXML
    private void onCancel() {
        // Check if form has changes (optional implementation)
        clearForm();
        showFeedback("Operación cancelada", "info");
        ViewSwitcher.switchTo(ExternalPaymentView.MAIN.getView());
    }

    /**
     * Handler for the Submit button.
     */
    @FXML
    private void onSubmit() {
        if (!isFormValid()) {
            showFeedback("Por favor corrija los errores antes de enviar", "error");
            return;
        }

        try {
            ExternalServiceRegistrationRequestDto request = createRequestDto();
            ValidationResultDto validationResult = service.validateServiceData(request);

            if (!validationResult.isValid()) {
                showFeedback("El formulario contiene errores de validación", "error");
                return;
            }

            ExternalServiceDto submitted = service.submitRegistration(request);

            showFeedback("Solicitud enviada exitosamente. ID: " + submitted.getId(), "success");
            clearForm();
        } catch (IllegalArgumentException e) {
            showFeedback("Error al enviar: " + e.getMessage(), "error");
        } catch (Exception e) {
            showFeedback("Error inesperado: " + e.getMessage(), "error");
        }
    }

    /**
     * Creates a request DTO from the current form values.
     *
     * @return the populated DTO
     */
    private ExternalServiceRegistrationRequestDto createRequestDto() {
        return ExternalServiceRegistrationRequestDto.builder()
                .providerName(providerNameField.getText().trim())
                .accountReference(accountReferenceField.getText().trim())
                .phoneCountryCode(phoneCountryCodeComboBox.getValue())
                .phoneNumber(phoneNumberField.getText().trim())
                .email(emailField.getText().trim())
                .contactDetails(contactDetailsArea.getText().trim())
                .build();
    }

    /**
     * Clears all form fields.
     */
    private void clearForm() {
        providerNameField.clear();
        accountReferenceField.clear();
        phoneNumberField.clear();
        emailField.clear();
        contactDetailsArea.clear();
        phoneCountryCodeComboBox.setValue("+591");

        hideAllErrors();
        disableSubmitButton();
        hideFeedback();
    }

    /**
     * Hides all error labels.
     */
    private void hideAllErrors() {
        hideFieldError(providerNameField, providerNameError);
        hideFieldError(accountReferenceField, accountReferenceError);
        hideFieldError(phoneNumberField, phoneNumberError);
        hideFieldError(emailField, emailError);
    }

    /**
     * Shows an error message for a specific field.
     *
     * @param field the text field with the error
     * @param errorLabel the label to display the error message
     * @param message the error message
     */
    private void showFieldError(TextField field, Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        field.getStyleClass().removeAll("valid", "error");
        field.getStyleClass().add("error");
    }

    /**
     * Hides the error message for a specific field.
     *
     * @param field the text field
     * @param errorLabel the error label to hide
     */
    private void hideFieldError(TextField field, Label errorLabel) {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        field.getStyleClass().removeAll("valid", "error");
        if (!field.getText().isEmpty()) {
            field.getStyleClass().add("valid");
        }
    }

    /**
     * Shows a feedback message to the user.
     *
     * @param message the message to display
     * @param type the type of message (success, error, warning, info)
     */
    private void showFeedback(String message, String type) {
        feedbackLabel.setText(message);
        feedbackLabel.getStyleClass().removeAll("success", "error", "warning", "info");
        feedbackLabel.getStyleClass().add(type);
        feedbackLabel.setVisible(true);
        feedbackLabel.setManaged(true);
    }

    /**
     * Hides the feedback message.
     */
    private void hideFeedback() {
        feedbackLabel.setVisible(false);
        feedbackLabel.setManaged(false);
    }
}

