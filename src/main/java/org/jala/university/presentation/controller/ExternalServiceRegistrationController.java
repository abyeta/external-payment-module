package org.jala.university.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.application.dto.ExternalServiceRegistrationRequestDto;
import org.jala.university.application.dto.HolderDto;
import org.jala.university.application.dto.RegistrationDocumentDto;
import org.jala.university.application.dto.ValidationResultDto;
import org.jala.university.application.mapper.ExternalServiceMapper;
import org.jala.university.application.mapper.RegistrationDocumentMapper;
import org.jala.university.application.service.ExternalServiceRegistrationService;
import org.jala.university.application.service.ExternalServiceRegistrationServiceImpl;
import org.jala.university.application.service.RegistrationDocumentService;
import org.jala.university.application.service.RegistrationDocumentServiceImpl;
import org.jala.university.application.validator.ServiceDataValidator;
import org.jala.university.application.validator.ValidationConstants;
import org.jala.university.commons.presentation.BaseController;
import org.jala.university.commons.presentation.ViewSwitcher;
import org.jala.university.domain.repository.ExternalServiceRepository;
import org.jala.university.domain.repository.HolderRepository;
import org.jala.university.domain.repository.RegistrationDocumentRepository;
import org.jala.university.infrastructure.persistance.ExternalServiceRepositoryImpl;
import org.jala.university.infrastructure.persistance.HolderRepositoryImpl;
import org.jala.university.infrastructure.persistance.RegistrationDocumentRepositoryImpl;
import org.jala.university.presentation.ExternalPaymentView;
import org.jala.university.presentation.store.ExternalServiceDataStore;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the External Service Registration view.
 * Handles user interactions, validation, and communication with the service layer.
 */
public class ExternalServiceRegistrationController extends BaseController {

    private static final int MAX_PROVIDER_NAME_LENGTH = 100;
    private static final int MAX_HOLDER_NAME_LENGTH = 150;
    private static final int MIN_HOLDER_NAME_LENGTH = 3;
    private static final int ACCOUNT_REFERENCE_LENGTH = 10;
    private static final int PHONE_NUMBER_LENGTH = 10;
    private static final int BYTES_PER_KB = 1024;
    private static final int BYTES_PER_MB = 1024;
    private static final double NAVIGATION_DELAY_SECONDS = 1.5;
    private static final int FILE_ITEM_SPACING = 4;
    private static final int FILE_ITEM_PADDING = 12;
    private static final int FILE_ITEM_HBOX_SPACING = 10;
    private static final int LANDLINE_LENGTH = 8;
    private static final int MAX_HOLDER_ID_LENGTH = 20;
    private static final int MIN_HOLDER_ID_LENGTH = 5;

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
    private DatePicker contractExpirationDatePicker;

    @FXML
    private TextArea contactDetailsArea;

    @FXML
    private TextField holderNameField;

    @FXML
    private TextField holderIdField;

    @FXML
    private TextField holderEmailField;

    @FXML
    private TextField landlinePhoneField;

    // FXML Components - Labels
    @FXML
    private Label providerNameError;

    @FXML
    private Label accountReferenceError;

    @FXML
    private Label phoneNumberError;

    @FXML
    private Label emailError;

    @FXML
    private Label holderNameError;

    @FXML
    private Label holderIdError;

    @FXML
    private Label holderEmailError;

    @FXML
    private Label landlinePhoneError;

    // FXML Components - Buttons
    @FXML
    private Button cancelButton;

    @FXML
    private Button submitButton;

    // FXML Components - Feedback
    @FXML
    private Label feedbackLabel;

    // FXML Components - File Upload
    @FXML
    private VBox dropZone;

    @FXML
    private VBox filesListContainer;

    @FXML
    private VBox filesListBox;

    @FXML
    private Label uploadError;

    @FXML
    private Button selectFilesButton;

    // Services and dependencies
    private ExternalServiceRegistrationService service;
    private RegistrationDocumentService documentService;
    private ServiceDataValidator validator;
    private EntityManager entityManager;

    // File management
    private List<File> selectedFiles = new ArrayList<>();

    /**
     * Init services for controller.
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
        entityManager = Persistence
                .createEntityManagerFactory("external-payment-pu")
                .createEntityManager();
        ExternalServiceRepository repository = new ExternalServiceRepositoryImpl(entityManager);
        RegistrationDocumentRepository documentRepository =
                new RegistrationDocumentRepositoryImpl(entityManager);

        // Initialize mappers and services
        HolderRepository holderRepository = new HolderRepositoryImpl(entityManager);
        // Initialize mapper and service
        ExternalServiceMapper mapper = new ExternalServiceMapper();
        service = new ExternalServiceRegistrationServiceImpl(
                repository, mapper, validator, holderRepository);

        RegistrationDocumentMapper documentMapper = new RegistrationDocumentMapper();
        documentService = new RegistrationDocumentServiceImpl(
                documentRepository, repository, documentMapper);
    }

    private void setupTextFormatters() {
        // Provider name: max 100 characters
        providerNameField.setTextFormatter(createMaxLengthFormatter(MAX_PROVIDER_NAME_LENGTH));

        // Account reference: only digits, max 10
        accountReferenceField.setTextFormatter(createNumericFormatter(ACCOUNT_REFERENCE_LENGTH));

        // Phone number: only digits, max 10
        phoneNumberField.setTextFormatter(createNumericFormatter(PHONE_NUMBER_LENGTH));

        holderNameField.setTextFormatter(createMaxLengthFormatter(MAX_HOLDER_NAME_LENGTH));
        holderIdField.setTextFormatter(createNumericFormatter(MAX_HOLDER_ID_LENGTH));
        landlinePhoneField.setTextFormatter(createNumericFormatter(LANDLINE_LENGTH));
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

        holderNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateHolderNameField();
            updateSubmitButtonState();
        });

        holderIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateHolderIdField();
            updateSubmitButtonState();
        });

        holderEmailField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateHolderEmailField();
            updateSubmitButtonState();
        });

        landlinePhoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateLandlinePhoneField();
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

    private boolean validateHolderNameField() {
        String value = holderNameField.getText();
        if (value == null || value.trim().isEmpty()) {
            showFieldError(holderNameField, holderNameError, "El nombre del titular es requerido");
            return false;
        } else if (value.trim().length() < MIN_HOLDER_NAME_LENGTH) {
            showFieldError(holderNameField, holderNameError, "El nombre debe tener al menos 3 caracteres");
            return false;
        } else {
            hideFieldError(holderNameField, holderNameError);
            return true;
        }
    }

    private boolean validateHolderIdField() {
        String value = holderIdField.getText();
        if (value == null || value.trim().isEmpty()) {
            showFieldError(holderIdField, holderIdError, "El ID del titular es requerido");
            return false;
        } else if (value.trim().length() < MIN_HOLDER_ID_LENGTH) {
            showFieldError(holderIdField, holderIdError, "El ID debe tener al menos 5 caracteres");
            return false;
        } else {
            hideFieldError(holderIdField, holderIdError);
            return true;
        }
    }

    private boolean validateHolderEmailField() {
        String value = holderEmailField.getText();
        var error = validator.validateEmail(value);

        if (error != null) {
            showFieldError(holderEmailField, holderEmailError, error.getMessage());
            return false;
        } else {
            hideFieldError(holderEmailField, holderEmailError);
            return true;
        }
    }

    private boolean validateLandlinePhoneField() {
        String value = landlinePhoneField.getText();
        if (value == null || value.trim().isEmpty()) {
            showFieldError(landlinePhoneField, landlinePhoneError, "El teléfono fijo es requerido");
            return false;
        } else if (value.length() != LANDLINE_LENGTH) {
            showFieldError(landlinePhoneField, landlinePhoneError, "Debe tener exactamente 8 dígitos");
            return false;
        } else {
            hideFieldError(landlinePhoneField, landlinePhoneError);
            return true;
        }
    }

    private boolean isFormValid() {
        return validateProviderNameField()
                && validateAccountReferenceField()
                && validatePhoneNumberField()
                && validateEmailField()
                && validateHolderNameField()
                && validateHolderIdField()
                && validateHolderEmailField()
                && validateLandlinePhoneField();
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
                displayValidationErrors(validationResult);
                return;
            }

            // Submit the service registration (transaction handled by repository/service layer)
            ExternalServiceDto submitted = service.submitRegistration(request);

            // Save the uploaded documents if any
            if (!selectedFiles.isEmpty()) {
                saveDocuments(submitted.getId());
            }

            ExternalServiceDataStore.get().add(submitted);

            showFeedback("Servicio registrado exitosamente. ID: " + submitted.getId(), "success");
            clearForm();

            // Navigate back after a brief delay
            javafx.animation.PauseTransition pause =
                    new javafx.animation.PauseTransition(
                            javafx.util.Duration.seconds(NAVIGATION_DELAY_SECONDS));
            pause.setOnFinished(e -> ViewSwitcher.switchTo(ExternalPaymentView.MAIN.getView()));
            pause.play();

        } catch (IllegalArgumentException e) {
            showFeedback("Error al enviar: " + e.getMessage(), "error");
        } catch (Exception e) {
            showFeedback("Error inesperado: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    private void saveDocuments(java.util.UUID serviceId) {
        try {
            List<RegistrationDocumentDto> documentDtos = new ArrayList<>();

            for (File file : selectedFiles) {
                RegistrationDocumentDto dto = RegistrationDocumentDto.builder()
                        .fileName(file.getName())
                        .file(file)
                        .createdAt(java.time.LocalDateTime.now())
                        .build();
                documentDtos.add(dto);
            }

            documentService.saveRegistrationDocuments(serviceId, documentDtos);
        } catch (Exception e) {
            showFeedback("Advertencia: El servicio se guardó pero hubo un error al guardar "
                    + "los documentos: " + e.getMessage(), "warning");
            e.printStackTrace();
        }
    }

    private void displayValidationErrors(ValidationResultDto result) {
        if (result.getErrors() != null && !result.getErrors().isEmpty()) {
            StringBuilder errorMessages = new StringBuilder();
            result.getErrors().forEach(error -> {
                errorMessages.append(error.getMessage()).append("\n");

                // Show error in specific field if applicable
                if ("files".equals(error.getField())) {
                    showUploadError(error.getMessage());
                }
            });
        }
    }

    private ExternalServiceRegistrationRequestDto createRequestDto() {
        return ExternalServiceRegistrationRequestDto.builder()
                .providerName(providerNameField.getText().trim())
                .accountReference(accountReferenceField.getText().trim())
                .phoneCountryCode(phoneCountryCodeComboBox.getValue())
                .phoneNumber(phoneNumberField.getText().trim())
                .email(emailField.getText().trim())
                .contractExpiration(contractExpirationDatePicker.getValue())
                .contactDetails(contactDetailsArea.getText().trim())
                .holder(HolderDto.builder()
                        .name(holderNameField.getText().trim())
                        .identificationNumber(holderIdField.getText().trim())
                        .email(holderEmailField.getText().trim())
                        .landlinePhone(landlinePhoneField.getText().trim())
                        .build())
                .files(selectedFiles)
                .build();
    }

    // ==================== FILE UPLOAD METHODS ====================

    @FXML
    private void onSelectFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Documentos");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
        );

        List<File> files = fileChooser.showOpenMultipleDialog(selectFilesButton.getScene().getWindow());
        if (files != null && !files.isEmpty()) {
            addFiles(files);
        }
    }

    @FXML
    private void onDragOver(DragEvent event) {
        if (event.getGestureSource() != dropZone && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    @FXML
    private void onDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean success = false;

        if (dragboard.hasFiles()) {
            List<File> files = dragboard.getFiles();
            addFiles(files);
            success = true;
        }

        event.setDropCompleted(success);
        event.consume();
    }

    private void addFiles(List<File> files) {
        hideUploadError();

        for (File file : files) {
            // Check if file is already added
            if (isFileAlreadyAdded(file)) {
                showUploadError("El archivo '" + file.getName() + "' ya fue agregado.");
                continue;
            }

            // Validate file type
            if (!isValidFileType(file)) {
                showUploadError("Tipo de archivo inválido: " + file.getName()
                        + ". Solo se permiten archivos PDF.");
                continue;
            }

            // Validate file size
            if (!isValidFileSize(file)) {
                showUploadError("El archivo '" + file.getName()
                        + "' excede el tamaño máximo de 10 MB.");
                continue;
            }

            // Add file to list
            selectedFiles.add(file);
        }

        // Update UI
        updateFilesListDisplay();
        updateSubmitButtonState();
    }

    private boolean isFileAlreadyAdded(File file) {
        return selectedFiles.stream()
                .anyMatch(f -> f.getAbsolutePath().equals(file.getAbsolutePath()));
    }


    private boolean isValidFileType(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".pdf");
    }

    private boolean isValidFileSize(File file) {
        long fileSizeInMB = file.length() / (BYTES_PER_KB * BYTES_PER_MB);
        return fileSizeInMB <= ValidationConstants.MAX_FILE_SIZE;
    }

    private void updateFilesListDisplay() {
        filesListBox.getChildren().clear();

        if (selectedFiles.isEmpty()) {
            filesListContainer.setVisible(false);
            filesListContainer.setManaged(false);
        } else {
            filesListContainer.setVisible(true);
            filesListContainer.setManaged(true);

            for (File file : selectedFiles) {
                HBox fileItem = createFileItemNode(file);
                filesListBox.getChildren().add(fileItem);
            }
        }
    }

    private HBox createFileItemNode(File file) {
        HBox container = new HBox(FILE_ITEM_HBOX_SPACING);
        container.setAlignment(Pos.CENTER_LEFT);
        container.getStyleClass().add("file-item");
        container.getStyleClass().add("success");
        container.setPadding(new Insets(FILE_ITEM_PADDING));

        // File icon
        Label iconLabel = new Label("📄");
        iconLabel.setStyle("-fx-font-size: 24px;");

        // File info VBox
        VBox fileInfo = new VBox(FILE_ITEM_SPACING);
        HBox.setHgrow(fileInfo, Priority.ALWAYS);

        Label fileName = new Label(file.getName());
        fileName.getStyleClass().add("file-item-name");

        long fileSizeInKB = file.length() / BYTES_PER_KB;
        String sizeText = fileSizeInKB < BYTES_PER_KB
                ? String.format("%d KB", fileSizeInKB)
                : String.format("%.2f MB", fileSizeInKB / (double) BYTES_PER_MB);

        Label fileSize = new Label(sizeText);
        fileSize.getStyleClass().add("file-item-size");

        Label status = new Label("✓ Uploaded");
        status.getStyleClass().add("file-item-status");

        fileInfo.getChildren().addAll(fileName, fileSize, status);

        // Remove button
        Button removeButton = new Button("✕");
        removeButton.getStyleClass().add("btn-remove-file");
        removeButton.setOnAction(e -> removeFile(file));

        container.getChildren().addAll(iconLabel, fileInfo, removeButton);

        return container;
    }

    private void removeFile(File file) {
        selectedFiles.remove(file);
        updateFilesListDisplay();
        updateSubmitButtonState();
        hideUploadError();
    }

    private void showUploadError(String message) {
        uploadError.setText(message);
        uploadError.setVisible(true);
        uploadError.setManaged(true);
    }

    private void hideUploadError() {
        uploadError.setVisible(false);
        uploadError.setManaged(false);
    }


    private void clearForm() {
        providerNameField.clear();
        accountReferenceField.clear();
        phoneNumberField.clear();
        emailField.clear();
        contactDetailsArea.clear();
        holderNameField.clear();
        holderIdField.clear();
        holderEmailField.clear();
        landlinePhoneField.clear();
        contractExpirationDatePicker.setValue(null);
        phoneCountryCodeComboBox.setValue("+591");

        // Clear file selection
        selectedFiles.clear();
        updateFilesListDisplay();
        hideUploadError();

        hideAllErrors();
        disableSubmitButton();
        hideFeedback();
    }

    private void hideAllErrors() {
        hideFieldError(providerNameField, providerNameError);
        hideFieldError(accountReferenceField, accountReferenceError);
        hideFieldError(phoneNumberField, phoneNumberError);
        hideFieldError(emailField, emailError);
        hideFieldError(holderNameField, holderNameError);
        hideFieldError(holderIdField, holderIdError);
        hideFieldError(holderEmailField, holderEmailError);
        hideFieldError(landlinePhoneField, landlinePhoneError);
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

