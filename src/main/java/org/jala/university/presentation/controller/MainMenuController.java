package org.jala.university.presentation.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.application.mapper.ExternalServiceMapper;
import org.jala.university.application.service.ExternalServiceRegistrationService;
import org.jala.university.application.service.ExternalServiceRegistrationServiceImpl;
import org.jala.university.application.validator.ServiceDataValidator;
import org.jala.university.commons.presentation.BaseController;
import org.jala.university.commons.presentation.ViewSwitcher;
import org.jala.university.domain.repository.HolderRepository;
import org.jala.university.infrastructure.persistance.ExternalServiceRepositoryImpl;
import org.jala.university.infrastructure.persistance.HolderRepositoryImpl;
import org.jala.university.presentation.ExternalPaymentView;
import org.jala.university.presentation.store.ExternalServiceDataStore;

public class MainMenuController extends BaseController {

    @FXML
    private VBox servicesTableContainer;
    @FXML
    private Label message;

    /**
     * Inicializa el menú principal:
     * construye el servicio, llena el store si está vacío
     * y renderiza la tabla de servicios.
     */
    @FXML
    public final void initialize() {
        // Construir servicio (JPA/Hibernate)
        final EntityManager em = Persistence
                .createEntityManagerFactory("external-payment-pu")
                .createEntityManager();

        final ExternalServiceRepositoryImpl repo = new ExternalServiceRepositoryImpl(em);
        final HolderRepository holderRepository = new HolderRepositoryImpl(em);
        final ExternalServiceMapper mapper = new ExternalServiceMapper();
        final ServiceDataValidator validator = new ServiceDataValidator();
        ExternalServiceRegistrationService service = new ExternalServiceRegistrationServiceImpl(
                repo, mapper, validator, holderRepository);

        final ExternalServiceDataStore store = ExternalServiceDataStore.get();
        if (store.masterList().isEmpty()) {
            store.setAll(service.findAll());
        }

        renderTable();
    }

    private void editService(final ExternalServiceDto dto) {
        message.setText("✏ Editando: " + dto.getProviderName());
        message.setVisible(true);
        message.setManaged(true);
    }

    private void toggleService(final ExternalServiceDto dto) {
        final String action = dto.isEnabled() ? "deshabilitando" : "habilitando";
        message.setText("⏸ Cambiando estado: " + action + " " + dto.getProviderName());
        message.setVisible(true);
        message.setManaged(true);
    }

    private void deleteService(final ExternalServiceDto dto) {
        message.setText("🗑 Eliminando: " + dto.getProviderName());
        message.setVisible(true);
        message.setManaged(true);
    }

    @FXML
    private void onRegisterService() {
        ViewSwitcher.switchTo(ExternalPaymentView.EXTERNAL_SERVICE_REGISTRATION.getView());
    }

    private void renderTable() {
        servicesTableContainer.getChildren().clear();

        final ExternalServiceDataStore store = ExternalServiceDataStore.get();
        final java.util.List<ExternalServiceDto> services = store.masterList();

        if (services.isEmpty()) {
            Label emptyLabel = new Label("No hay servicios registrados");
            emptyLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 14px; -fx-padding: 40; -fx-alignment: center;");
            servicesTableContainer.getChildren().add(emptyLabel);
            return;
        }

        for (ExternalServiceDto dto : services) {
            final HBox row = createTableRow(dto);
            servicesTableContainer.getChildren().add(row);
        }
    }

    private HBox createTableRow(final ExternalServiceDto dto) {
        final int rowSpacing = 20;
        final int rowPadding = 18;
        final int codeWidth = 200;
        final int statusWidth = 150;
        final int actionsWidth = 150;

        HBox row = new HBox(rowSpacing);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
                "-fx-background-color: white; "
                        + "-fx-padding: " + rowPadding + " 20; "
                        + "-fx-border-color: #e0e0e0; "
                        + "-fx-border-width: 0 0 1 0;"
        );

        Label nameLabel = new Label(dto.getProviderName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #1a1a1a; -fx-font-weight: normal;");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        Label codeLabel = new Label(dto.getAccountReference());
        codeLabel.setStyle(
                "-fx-font-size: 13px; "
                        + "-fx-text-fill: #6c757d; "
                        + "-fx-font-family: monospace; "
                        + "-fx-min-width: " + codeWidth + "px; "
                        + "-fx-pref-width: " + codeWidth + "px; "
                        + "-fx-max-width: " + codeWidth + "px;"
        );

        HBox statusBox = createStatusBadge(dto, statusWidth);
        HBox actionsBox = createActionsBox(dto, actionsWidth);

        row.getChildren().addAll(nameLabel, codeLabel, statusBox, actionsBox);
        addRowHoverEffect(row);

        return row;
    }

    private HBox createStatusBadge(final ExternalServiceDto dto, final int statusWidth) {
        final int badgePadding = 6;
        final int badgeFontSize = 12;
        final int badgeRadius = 12;

        HBox statusBox = new HBox();
        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusBox.setMinWidth(statusWidth);
        statusBox.setPrefWidth(statusWidth);
        statusBox.setMaxWidth(statusWidth);

        Label statusBadge = new Label(dto.isEnabled() ? "● Habilitado" : "● Deshabilitado");
        if (dto.isEnabled()) {
            statusBadge.setStyle(
                    "-fx-background-color: #d4edda; "
                            + "-fx-text-fill: #28a745; "
                            + "-fx-padding: " + badgePadding + " 12; "
                            + "-fx-background-radius: " + badgeRadius + "px; "
                            + "-fx-font-size: " + badgeFontSize + "px; "
                            + "-fx-font-weight: bold;"
            );
        } else {
            statusBadge.setStyle(
                    "-fx-background-color: #e9ecef; "
                            + "-fx-text-fill: #6c757d; "
                            + "-fx-padding: " + badgePadding + " 12; "
                            + "-fx-background-radius: " + badgeRadius + "px; "
                            + "-fx-font-size: " + badgeFontSize + "px; "
                            + "-fx-font-weight: bold;"
            );
        }
        statusBox.getChildren().add(statusBadge);
        return statusBox;
    }

    private HBox createActionsBox(final ExternalServiceDto dto, final int actionsWidth) {
        final int buttonSpacing = 8;
        final int buttonSize = 32;

        HBox actionsBox = new HBox(buttonSpacing);
        actionsBox.setAlignment(Pos.CENTER);
        actionsBox.setMinWidth(actionsWidth);
        actionsBox.setPrefWidth(actionsWidth);
        actionsBox.setMaxWidth(actionsWidth);

        Button editBtn = createActionButton("✏", "#6c757d", buttonSize);
        editBtn.setOnAction(e -> editService(dto));

        Button toggleBtn = createActionButton("⏸", "#6c757d", buttonSize);
        toggleBtn.setOnAction(e -> toggleService(dto));

        Button deleteBtn = createActionButton("🗑", "#dc3545", buttonSize);
        deleteBtn.setOnAction(e -> deleteService(dto));

        actionsBox.getChildren().addAll(editBtn, toggleBtn, deleteBtn);
        return actionsBox;
    }

    private Button createActionButton(final String icon, final String color, final int size) {
        final int fontSize = 16;
        final int padding = 5;
        final int radius = 4;

        Button btn = new Button(icon);
        btn.setStyle(
                "-fx-background-color: transparent; "
                        + "-fx-text-fill: " + color + "; "
                        + "-fx-font-size: " + fontSize + "px; "
                        + "-fx-cursor: hand; "
                        + "-fx-min-width: " + size + "px; "
                        + "-fx-min-height: " + size + "px; "
                        + "-fx-padding: " + padding + ";"
        );

        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: " + (color.equals("#dc3545") ? "#f8d7da" : "#f8f9fa") + "; "
                        + "-fx-text-fill: " + color + "; "
                        + "-fx-font-size: " + fontSize + "px; "
                        + "-fx-cursor: hand; "
                        + "-fx-min-width: " + size + "px; "
                        + "-fx-min-height: " + size + "px; "
                        + "-fx-padding: " + padding + "; "
                        + "-fx-background-radius: " + radius + "px;"
        ));

        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent; "
                        + "-fx-text-fill: " + color + "; "
                        + "-fx-font-size: " + fontSize + "px; "
                        + "-fx-cursor: hand; "
                        + "-fx-min-width: " + size + "px; "
                        + "-fx-min-height: " + size + "px; "
                        + "-fx-padding: " + padding + ";"
        ));

        return btn;
    }

    private void addRowHoverEffect(final HBox row) {
        final int rowPadding = 18;
        final int horizontalPadding = 20;
        row.setOnMouseEntered(e -> row.setStyle(
                "-fx-background-color: #f8f9fa; "
                        + "-fx-padding: " + rowPadding + " " + horizontalPadding + "; "
                        + "-fx-border-color: #e0e0e0; "
                        + "-fx-border-width: 0 0 1 0;"
        ));

        row.setOnMouseExited(e -> row.setStyle(
                "-fx-background-color: white; "
                        + "-fx-padding: " + rowPadding + " " + horizontalPadding + "; "
                        + "-fx-border-color: #e0e0e0; "
                        + "-fx-border-width: 0 0 1 0;"
        ));
    }
}
