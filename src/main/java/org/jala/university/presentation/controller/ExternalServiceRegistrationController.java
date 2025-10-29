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
import org.jala.university.application.service.ExternalServiceRegistrationServiceImpl;
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
     * Initializes the controller after FXML injection is complete.
     * Sets up services, text formatters, combo box values, field validators, and button states.
     * This method is automatically called by JavaFX after the FXML file is loaded.
     * Subclasses can override this method but should call super.initialize() first.
     */
    @FXML
    public void initialize() {
        initializeServices();
        setupTextFormatters();
        populateCountryCodeComboBox();
        setupFieldValidators();
        disableSubmitButton();
    }


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
        service = new ExternalServiceRegistrationServiceImpl(
                repository, mapper, validator);
    }

    private void setupTextFormatters() {
        // Provider name: max 100 characters
        providerNameField.setTextFormatter(createMaxLengthFormatter(MAX_PROVIDER_NAME_LENGTH));

        // Account reference: only digits, max 10
        accountReferenceField.setTextFormatter(createNumericFormatter(ACCOUNT_REFERENCE_LENGTH));

        // Phone number: only digits, max 10
        phoneNumberField.setTextFormatter(createNumericFormatter(PHONE_NUMBER_LENGTH));
    }

    private TextFormatter<String> createNumericFormatter(int maxLength) {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*") && newText.length() <= maxLength) {
                return change;
            }
            return null;
        });
    }

    private TextFormatter<String> createMaxLengthFormatter(int maxLength) {
        return new TextFormatter<>(change -> {
            if (change.getControlNewText().length() <= maxLength) {
                return change;
            }
            return null;
        });
    }

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

    private void setupFieldValidators() {
        // Provider name validation
        providerNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateProviderNameField();
          //  updateSubmitButtonState();
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

    private boolean isFormValid() {
        return validateProviderNameField()
                && validateAccountReferenceField()
                && validatePhoneNumberField()
                && validateEmailField();
    }

    private void updateSubmitButtonState() {
        submitButton.setDisable(!isFormValid());
    }

    private void disableSubmitButton() {
        submitButton.setDisable(true);
    }

    @FXML
    private void onBackToMain() {
        ViewSwitcher.switchTo(ExternalPaymentView.MAIN.getView());
    }

    @FXML
    private void onCancel() {
        // Check if form has changes (optional implementation)
        clearForm();
        showFeedback("Operación cancelada", "info");
        ViewSwitcher.switchTo(ExternalPaymentView.MAIN.getView());
    }

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
            showFeedback("Operación cancelada", "info");
            ViewSwitcher.switchTo(ExternalPaymentView.MAIN.getView());
        } catch (IllegalArgumentException e) {
            showFeedback("Error al enviar: " + e.getMessage(), "error");
        } catch (Exception e) {
            showFeedback("Error inesperado: " + e.getMessage(), "error");
        }
    }

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

    private void hideAllErrors() {
        hideFieldError(providerNameField, providerNameError);
        hideFieldError(accountReferenceField, accountReferenceError);
        hideFieldError(phoneNumberField, phoneNumberError);
        hideFieldError(emailField, emailError);
    }

    private void showFieldError(TextField field, Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        field.getStyleClass().removeAll("valid", "error");
        field.getStyleClass().add("error");
    }


    private void hideFieldError(TextField field, Label errorLabel) {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        field.getStyleClass().removeAll("valid", "error");
        if (!field.getText().isEmpty()) {
            field.getStyleClass().add("valid");
        }
    }

    private void showFeedback(String message, String type) {
        feedbackLabel.setText(message);
        feedbackLabel.getStyleClass().removeAll("success", "error", "warning", "info");
        feedbackLabel.getStyleClass().add(type);
        feedbackLabel.setVisible(true);
        feedbackLabel.setManaged(true);
    }

    private void hideFeedback() {
        feedbackLabel.setVisible(false);
        feedbackLabel.setManaged(false);
    }
}

