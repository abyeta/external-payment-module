package org.jala.university.presentation.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jala.university.application.dto.*;
import org.jala.university.application.factory.ServiceFactory;
import org.jala.university.application.service.ExternalServiceUpdateService;
import org.jala.university.application.service.RegistrationDocumentService;
import org.jala.university.application.validator.ServiceDataValidator;
import org.jala.university.application.validator.ValidationConstants;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.entity.Holder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ExternalServiceUpdateController {

    private static final int MAX_PROVIDER_NAME_LENGTH = 100;
    private static final int MAX_HOLDER_NAME_LENGTH = 150;
    private static final int MIN_HOLDER_NAME_LENGTH = 3;
    private static final int ACCOUNT_REFERENCE_LENGTH = 10;
    private static final int PHONE_NUMBER_LENGTH = 10;
    private static final int BYTES_PER_KB = 1024;
    private static final int BYTES_PER_MB = 1024;
    private static final int FILE_ITEM_SPACING = 4;
    private static final int FILE_ITEM_PADDING = 12;
    private static final int FILE_ITEM_HBOX_SPACING = 10;
    private static final int LANDLINE_LENGTH = 8;
    private static final int MAX_HOLDER_ID_LENGTH = 20;
    private static final int MIN_HOLDER_ID_LENGTH = 5;

    @FXML private Label pageTitleLabel;
    @FXML private TextField providerNameField;
    @FXML private TextField accountReferenceField;
    @FXML private ComboBox<String> phoneCountryCodeComboBox;
    @FXML private TextField phoneNumberField;
    @FXML private TextField emailField;
    @FXML private DatePicker contractExpirationDatePicker;
    @FXML private TextArea contactDetailsArea;
    @FXML private TextField holderNameField;
    @FXML private TextField holderIdField;
    @FXML private TextField holderEmailField;
    @FXML private TextField landlinePhoneField;

    @FXML private Label providerNameError;
    @FXML private Label accountReferenceError;
    @FXML private Label phoneNumberError;
    @FXML private Label emailError;
    @FXML private Label holderNameError;
    @FXML private Label holderIdError;
    @FXML private Label holderEmailError;
    @FXML private Label landlinePhoneError;

    @FXML private Button submitButton;
    @FXML private Button cancelButton;

    @FXML private VBox dropZone;
    @FXML private VBox filesListContainer;
    @FXML private VBox filesListBox;
    @FXML private Label uploadError;
    @FXML private Button selectFilesButton;

    private ExternalServiceUpdateService updateService;
    private RegistrationDocumentService documentService;
    private ServiceDataValidator validator;

    private final List<File> newFiles = new ArrayList<>();
    private final List<RegistrationDocumentDto> existingDocs = new ArrayList<>();
    private final List<File> tempExistingFiles = new ArrayList<>();
    private final List<UUID> deletedDocIds = new ArrayList<>();

    private UUID serviceId;
    private Runnable onSuccess;

    public void setForUpdate(UUID id, Runnable onSuccess) {
        this.serviceId = id;
        this.onSuccess = onSuccess;
        initializeServices();
        setupTextFormatters();
        populateCountryCodeComboBox();
        setupFieldValidators();
        disableSubmitButton();
        pageTitleLabel.setText("Actualizar Servicio");
        submitButton.setText("Actualizar");
        loadService();
    }

    private void initializeServices() {
        this.validator = ServiceFactory.getValidator();
        this.updateService = ServiceFactory.getUpdateService();
        this.documentService = ServiceFactory.getDocumentService();
    }

    private void setupTextFormatters() {
        providerNameField.setTextFormatter(createMaxLengthFormatter(MAX_PROVIDER_NAME_LENGTH));
        accountReferenceField.setTextFormatter(createNumericFormatter(ACCOUNT_REFERENCE_LENGTH));
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
                "+591", "+52", "+1", "+51", "+56",
                "+54", "+55", "+57", "+34", "+44"
        );
        phoneCountryCodeComboBox.setValue("+591");
    }

    private void setupFieldValidators() {
        providerNameField.textProperty().addListener((obs, old, val) -> {
            validateProviderNameField();
            updateSubmitButtonState();
        });
        accountReferenceField.textProperty().addListener((obs, old, val) -> {
            validateAccountReferenceField();
            updateSubmitButtonState();
        });
        phoneNumberField.textProperty().addListener((obs, old, val) -> {
            validatePhoneNumberField();
            updateSubmitButtonState();
        });
        emailField.textProperty().addListener((obs, old, val) -> {
            validateEmailField();
            updateSubmitButtonState();
        });
        phoneCountryCodeComboBox.valueProperty().addListener((obs, old, val) -> {
            validatePhoneNumberField();
            updateSubmitButtonState();
        });
        holderNameField.textProperty().addListener((obs, old, val) -> {
            validateHolderNameField();
            updateSubmitButtonState();
        });
        holderIdField.textProperty().addListener((obs, old, val) -> {
            validateHolderIdField();
            updateSubmitButtonState();
        });
        holderEmailField.textProperty().addListener((obs, old, val) -> {
            validateHolderEmailField();
            updateSubmitButtonState();
        });
        landlinePhoneField.textProperty().addListener((obs, old, val) -> {
            validateLandlinePhoneField();
            updateSubmitButtonState();
        });
    }

    private boolean validateProviderNameField() {
        var error = validator.validateProviderName(providerNameField.getText());
        if (error != null) {
            showFieldError(providerNameField, providerNameError, error.getMessage());
            return false;
        } else {
            hideFieldError(providerNameField, providerNameError);
            return true;
        }
    }

    private boolean validateAccountReferenceField() {
        var error = validator.validateAccountReference(accountReferenceField.getText());
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
        var countryError = validator.validatePhoneCountryCode(countryCode);
        var numberError = validator.validatePhoneNumber(number);

        if (countryError != null || numberError != null) {
            String message = countryError != null ? countryError.getMessage() : numberError.getMessage();
            showFieldError(phoneNumberField, phoneNumberError, message);
            return false;
        } else {
            hideFieldError(phoneNumberField, phoneNumberError);
            return true;
        }
    }

    private boolean validateEmailField() {
        var error = validator.validateEmail(emailField.getText());
        if (error != null) {
            showFieldError(emailField, emailError, error.getMessage());
            return false;
        } else {
            hideFieldError(emailField, emailError);
            return true;
        }
    }

    private boolean validateHolderNameField() {
        String val = holderNameField.getText().trim();
        if (val.isEmpty()) {
            showFieldError(holderNameField, holderNameError, "El nombre del titular es requerido");
            return false;
        } else if (val.length() < MIN_HOLDER_NAME_LENGTH) {
            showFieldError(holderNameField, holderNameError, "El nombre debe tener al menos 3 caracteres");
            return false;
        } else {
            hideFieldError(holderNameField, holderNameError);
            return true;
        }
    }

    private boolean validateHolderIdField() {
        String val = holderIdField.getText().trim();
        if (val.isEmpty()) {
            showFieldError(holderIdField, holderIdError, "El ID del titular es requerido");
            return false;
        } else if (val.length() < MIN_HOLDER_ID_LENGTH) {
            showFieldError(holderIdField, holderIdError, "El ID debe tener al menos 5 caracteres");
            return false;
        } else {
            hideFieldError(holderIdField, holderIdError);
            return true;
        }
    }

    private boolean validateHolderEmailField() {
        var error = validator.validateEmail(holderEmailField.getText());
        if (error != null) {
            showFieldError(holderEmailField, holderEmailError, error.getMessage());
            return false;
        } else {
            hideFieldError(holderEmailField, holderEmailError);
            return true;
        }
    }

    private boolean validateLandlinePhoneField() {
        String val = landlinePhoneField.getText();
        if (val.isEmpty()) {
            showFieldError(landlinePhoneField, landlinePhoneError, "El teléfono fijo es requerido");
            return false;
        } else if (val.length() != LANDLINE_LENGTH) {
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
        submitButton.setDisable(!isFormValid()
                || (existingDocs.stream().noneMatch(d -> !deletedDocIds.contains(d.getId()))
                && newFiles.isEmpty()));
    }

    private void disableSubmitButton() {
        submitButton.setDisable(true);
    }

    private void loadService() {
        try {
            ExternalServiceDto externalServiceDto = updateService.findById(serviceId);

            providerNameField.setText(externalServiceDto.getProviderName());
            accountReferenceField.setText(externalServiceDto.getAccountReference());
            phoneCountryCodeComboBox.setValue(externalServiceDto.getPhoneCountryCode());
            phoneNumberField.setText(externalServiceDto.getPhoneNumber());
            emailField.setText(externalServiceDto.getEmail());
            contractExpirationDatePicker.setValue(externalServiceDto.getContractExpiration());
            contactDetailsArea.setText(externalServiceDto.getContactDetails() != null
                    ? externalServiceDto.getContactDetails() : "");

            EntityManager em = Persistence.createEntityManagerFactory("external-payment-pu")
                    .createEntityManager();
            try {
                ExternalService service = em.find(ExternalService.class, serviceId);
                if (service != null && service.getHolder() != null) {
                    Holder h = service.getHolder();
                    holderNameField.setText(h.getName());
                    holderIdField.setText(h.getIdentificationNumber());
                    holderEmailField.setText(h.getEmail());
                    landlinePhoneField.setText(h.getLandlinePhone());
                }
            } finally {
                em.close();
            }

            List<RegistrationDocumentDto> docs = documentService.findAllRegistrationDocuments(serviceId);
            existingDocs.clear();
            tempExistingFiles.clear();

            for (RegistrationDocumentDto doc : docs) {
                File fileData = doc.getFile();
                String fileName = doc.getFileName();

                if (fileData != null && fileData.length() > 0) {
                    try {
                        Path tempPath = Files.createTempFile("doc_", "_" + fileName);
                        Files.write(tempPath, Files.readAllBytes(fileData.toPath()));
                        File tempFile = tempPath.toFile();
                        tempFile.deleteOnExit();

                        RegistrationDocumentDto fullDto = RegistrationDocumentDto.builder()
                                .id(doc.getId())
                                .fileName(fileName)
                                .file(tempFile)
                                .createdAt(doc.getCreatedAt())
                                .build();

                        existingDocs.add(fullDto);
                        tempExistingFiles.add(tempFile);
                    } catch (IOException e) {
                        showAlert("Error", "No se pudo cargar documento: " + fileName);
                    }
                }
            }

            updateFilesListDisplay();
            updateSubmitButtonState();

        } catch (Exception e) {
            showAlert("Error", "No se pudo cargar el servicio: " + e.getMessage());
        }
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }

    @FXML
    private void onSubmit() {
        if (!confirmUpdate()) {
            return;
        }

        clearErrors();

        // Validar que haya al menos un archivo (existente o nuevo)
        boolean hasExisting = existingDocs.stream().anyMatch(d -> !deletedDocIds.contains(d.getId()));
        if (!hasExisting && newFiles.isEmpty()) {
            showError(uploadError, "Al menos un archivo es requerido");
            return;
        }

        try {
            // Actualizar el servicio principal
            ExternalServiceRegistrationRequestDto request = buildRequest();
            ValidationResultDto validation = validator.validateForUpdate(request);
            if (!validation.isValid()) {
                showValidationErrors(validation.getErrors());
                return;
            }

            updateService.update(serviceId, request);

            // Eliminar documentos marcados para borrar
            if (!deletedDocIds.isEmpty()) {
                System.out.println("Eliminando " + deletedDocIds.size() + " documentos");
                updateService.deleteByIds(deletedDocIds);
                deletedDocIds.clear();
            }

            // Guardar nuevos archivos
            if (!newFiles.isEmpty()) {
                System.out.println("Guardando " + newFiles.size() + " nuevos archivos");
                for (File file : newFiles) {
                    System.out.println(" - " + file.getName() + " (" + file.length() + " bytes)");
                }

                List<RegistrationDocumentDto> newDtos = newFiles.stream()
                        .map(f -> RegistrationDocumentDto.builder()
                                .fileName(f.getName())
                                .file(f)
                                .createdAt(LocalDateTime.now())
                                .externalServiceId(serviceId)
                                .build())
                        .toList();

                documentService.saveRegistrationDocuments(serviceId, newDtos);
                newFiles.clear();
                System.out.println("Archivos guardados exitosamente");
            }

            showAlert("Éxito", "Servicio actualizado correctamente");
            if (onSuccess != null) {
                onSuccess.run();
            }
            close();

        } catch (Exception e) {
            showAlert("Error", "Error al actualizar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveNewFilesToDatabase(List<File> files) {
        for (File file : files) {
            try {

                byte[] fileContent = Files.readAllBytes(file.toPath());

                RegistrationDocumentDto documentDto = RegistrationDocumentDto.builder()
                        .fileName(file.getName())
                        .file(file) // Esto podría causar problemas
                        .createdAt(LocalDateTime.now())
                        .externalServiceId(serviceId)
                        .build();


                documentService.saveRegistrationDocuments(serviceId, List.of(documentDto));

            } catch (IOException e) {
                showAlert("Error", "No se pudo leer el archivo: " + file.getName());
                throw new RuntimeException(e);
            }
        }
    }

    private ExternalServiceRegistrationRequestDto buildRequest() {
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
                .files(null)
                .build();
    }

    private void showValidationErrors(List<ValidationErrorDto> errors) {
        errors.forEach(error -> {
            switch (error.getField()) {
                case ValidationConstants.FIELD_PROVIDER_NAME ->
                        showFieldError(providerNameField, providerNameError, error.getMessage());
                case ValidationConstants.FIELD_ACCOUNT_REFERENCE ->
                        showFieldError(accountReferenceField, accountReferenceError, error.getMessage());
                case ValidationConstants.FIELD_PHONE_COUNTRY_CODE, ValidationConstants.FIELD_PHONE_NUMBER ->
                        showFieldError(phoneNumberField, phoneNumberError, error.getMessage());
                case ValidationConstants.FIELD_EMAIL ->
                        showFieldError(emailField, emailError, error.getMessage());
                default ->
                        System.out.println("Campo de validación no manejado: " + error.getField());
            }
        });
    }

    private void clearErrors() {
        hideFieldError(providerNameField, providerNameError);
        hideFieldError(accountReferenceField, accountReferenceError);
        hideFieldError(phoneNumberField, phoneNumberError);
        hideFieldError(emailField, emailError);
        hideFieldError(holderNameField, holderNameError);
        hideFieldError(holderIdField, holderIdError);
        hideFieldError(holderEmailField, holderEmailError);
        hideFieldError(landlinePhoneField, landlinePhoneError);
        hideUploadError();
    }

    @FXML private void onCancel() {
        close();
    }

    @FXML private void onSelectFiles() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar Documentos");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        List<File> files = fc.showOpenMultipleDialog(selectFilesButton.getScene().getWindow());
        if (files != null && !files.isEmpty()) {
            addFiles(files);
        }
    }

    @FXML private void onDragOver(DragEvent e) {
        if (e.getGestureSource() != dropZone && e.getDragboard().hasFiles()) {
            e.acceptTransferModes(TransferMode.COPY);
        }
        e.consume();
    }

    @FXML private void onDragDropped(DragEvent e) {
        Dragboard db = e.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            addFiles(db.getFiles());
            success = true;
        }
        e.setDropCompleted(success);
        e.consume();
    }

    private void addFiles(List<File> files) {
        hideUploadError();
        for (File f : files) {
            if (isFileAlreadyAdded(f)) {
                showUploadError("El archivo '" + f.getName() + "' ya fue agregado.");
                continue;
            }
            if (!isValidFileType(f)) {
                showUploadError("Solo PDF: " + f.getName());
                continue;
            }
            if (!isValidFileSize(f)) {
                showUploadError("Máximo 10 MB: " + f.getName());
                continue;
            }
            newFiles.add(f);
        }
        updateFilesListDisplay();
        updateSubmitButtonState();
    }

    private boolean isFileAlreadyAdded(File f) {
        return newFiles.stream().anyMatch(n -> n.getAbsolutePath().equals(f.getAbsolutePath()))
                || existingDocs.stream().anyMatch(d -> d.getFileName().equals(f.getName()));
    }

    private boolean isValidFileType(File f) {
        return f.getName().toLowerCase().endsWith(".pdf");
    }

    private boolean isValidFileSize(File f) {
        return f.length() / (BYTES_PER_KB * BYTES_PER_MB) <= ValidationConstants.MAX_FILE_SIZE;
    }

    private void updateFilesListDisplay() {
        filesListBox.getChildren().clear();
        boolean hasFiles = false;

        for (RegistrationDocumentDto d : existingDocs) {
            if (!deletedDocIds.contains(d.getId())) {
                filesListBox.getChildren().add(createFileItemNode(d.getFileName(), true, d.getId(), d.getFile()));
                hasFiles = true;
            }
        }

        for (File f : newFiles) {
            filesListBox.getChildren().add(createFileItemNode(f.getName(), false, null, f));
            hasFiles = true;
        }

        filesListContainer.setVisible(hasFiles);
        filesListContainer.setManaged(hasFiles);
    }

    private HBox createFileItemNode(String name, boolean isExisting, UUID docId, File file) {
        HBox container = new HBox(FILE_ITEM_HBOX_SPACING);
        container.setAlignment(Pos.CENTER_LEFT);
        container.getStyleClass().add("file-item");
        container.getStyleClass().add("success");
        container.setPadding(new Insets(FILE_ITEM_PADDING));

        Label icon = new Label("PDF");
        icon.setStyle("-fx-font-size: 24px;");

        VBox info = new VBox(FILE_ITEM_SPACING);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label fileName = new Label(name);
        fileName.getStyleClass().add("file-item-name");

        long kb = (file != null && file.exists()) ? file.length() / BYTES_PER_KB : 0;
        String size = kb < BYTES_PER_KB ? kb + " KB" : String.format("%.2f MB", kb / (double) BYTES_PER_MB);
        Label fileSize = new Label(size);
        fileSize.getStyleClass().add("file-item-size");

        Label status = new Label(isExisting ? "Subido" : "Nuevo");
        status.getStyleClass().add("file-item-status");

        info.getChildren().addAll(fileName, fileSize, status);

        Button remove = new Button("X");
        remove.getStyleClass().add("btn-remove-file");
        remove.setOnAction(e -> {
            if (isExisting && docId != null) {
                existingDocs.removeIf(d -> d.getId().equals(docId));
                deletedDocIds.add(docId);
            } else {
                newFiles.removeIf(f -> f.getName().equals(name));
            }
            updateFilesListDisplay();
            updateSubmitButtonState();
            hideUploadError();
        });

        container.getChildren().addAll(icon, info, remove);
        return container;
    }

    private void showUploadError(String msg) {
        uploadError.setText(msg);
        uploadError.setVisible(true);
        uploadError.setManaged(true);
    }

    private void hideUploadError() {
        uploadError.setVisible(false);
        uploadError.setManaged(false);
    }

    private boolean confirmUpdate() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmar");
        a.setContentText("¿Guardar cambios?");
        return a.showAndWait().filter(r -> r == ButtonType.OK).isPresent();
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(title.equals("Éxito") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showFieldError(TextField f, Label l, String m) {
        l.setText(m);
        l.setVisible(true);
        l.setManaged(true);
        f.getStyleClass().removeAll("valid", "error");
        f.getStyleClass().add("error");
    }

    private void hideFieldError(TextField f, Label l) {
        l.setVisible(false);
        l.setManaged(false);
        f.getStyleClass().removeAll("valid", "error");
        if (!f.getText().isEmpty()) {
            f.getStyleClass().add("valid");
        }
    }

    @FXML private void close() {
        ((Stage) submitButton.getScene().getWindow()).close();
    }
}
