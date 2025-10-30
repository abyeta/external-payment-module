package org.jala.university.presentation.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
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
import org.jala.university.infrastructure.persistance.ExternalServiceRepositoryImpl;
import org.jala.university.presentation.ExternalPaymentView;
import org.jala.university.presentation.store.ExternalServiceDataStore;

public class MainMenuController extends BaseController {

  @FXML
  private GridPane root;
  @FXML
  private VBox rightMenu;
  @FXML
  private ScrollPane enabledServicesScroll;
  @FXML
  private ScrollPane disabledServicesScroll;
  @FXML
  private VBox enabledHBox;
  @FXML
  private VBox disabledHBox;
  @FXML
  private Button editButton;
  @FXML
  private Button disableButton;
  @FXML
  private Button deleteButton;
  @FXML
  private Button addButton;

  @FXML
  private Label message;

  private ExternalServiceRegistrationService service;
  private ExternalServiceDto selected;

  /**
   * Inicializa el menú principal:
   * construye el servicio, llena el store si está vacío
   * y renderiza las tarjetas (nombre + código).
   */
  @FXML
  public final void initialize() {
    // Construir servicio (JPA/Hibernate)
    final EntityManager em = Persistence
        .createEntityManagerFactory("external-payment-pu")
        .createEntityManager();

    final ExternalServiceRepositoryImpl repo = new ExternalServiceRepositoryImpl(em);
    final ExternalServiceMapper mapper = new ExternalServiceMapper();
    final ServiceDataValidator validator = new ServiceDataValidator();
    service = new ExternalServiceRegistrationServiceImpl(repo, mapper, validator);

    final ExternalServiceDataStore store = ExternalServiceDataStore.get();
    if (store.masterList().isEmpty()) {
      store.setAll(service.findAll());
    }


    renderLists();
  }

  public final void edit(ActionEvent event) {
    if (selected == null) {
      message.setText("selecciona un servicio");
      message.setVisible(true);
      return;
    }
    message.setText("editando " + selected.getProviderName() + " (" + selected.getAccountReference() + ")");
    message.setVisible(true);
  }

  public final void disable(ActionEvent event) {
    if (selected == null) {
      message.setText("selecciona un servicio");
      message.setVisible(true);
      return;
    }
    message.setText("deshabilitando " + selected.getProviderName() + " (" + selected.getAccountReference() + ")");
    message.setVisible(true);
  }

  public final void delete(ActionEvent event) {
    if (selected == null) {
      message.setText("selecciona un servicio");
      message.setVisible(true);
      return;
    }
    message.setText("borrando " + selected.getProviderName() + " (" + selected.getAccountReference() + ")");
    message.setVisible(true);
  }

  @FXML
  private void onRegisterService() {
    ViewSwitcher.switchTo(ExternalPaymentView.EXTERNAL_SERVICE_REGISTRATION.getView());
  }

  public final void enterServices(ActionEvent event) {
  }

  private void renderLists() {
    enabledHBox.getChildren().clear();
    disabledHBox.getChildren().clear();

    final ExternalServiceDataStore store = ExternalServiceDataStore.get();

    final var enabledList = store.masterList().stream()
        .filter(ExternalServiceDto::isEnabled)
        .toList();

    final var disabledList = store.masterList().stream()
        .filter(s -> !s.isEnabled())
        .toList();

    renderGroup(enabledHBox, enabledList);
    renderGroup(disabledHBox, disabledList);
  }


  private void renderGroup(final VBox container, final java.util.List<ExternalServiceDto> list) {
    final int gridSpacing = 10;
    final int columnsPerRow = 4;

    HBox row = new HBox(gridSpacing);
    int count = 0;

    for (ExternalServiceDto dto : list) {
      final VBox card = createServiceCard(dto.getProviderName(), dto.getAccountReference(), dto);
      row.getChildren().add(card);
      count++;
      if (count % columnsPerRow == 0) {
        container.getChildren().add(row);
        row = new HBox(gridSpacing);
      }
    }
    if (!row.getChildren().isEmpty()) {
      container.getChildren().add(row);
    }
  }

  private VBox createServiceCard(final String name, final String code, final ExternalServiceDto dto) {
    final int cardWidth = 150;
    final int cardHeight = 100;

    VBox card = new VBox();
    card.setStyle("-fx-background-color: rgba(0,98,255,0.44); -fx-padding: 8;");
    card.setPrefWidth(cardWidth);
    card.setMinWidth(cardWidth);
    card.setPrefHeight(cardHeight);
    card.setMinHeight(cardHeight);

    Label nameLbl = new Label(name);
    nameLbl.setStyle("-fx-font-size: 18;");
    HBox top = new HBox(nameLbl);
    top.setAlignment(Pos.TOP_LEFT);

    Label codeLbl = new Label(code);
    codeLbl.setStyle("-fx-font-size: 12; -fx-opacity: 0.8;");
    HBox bottom = new HBox(codeLbl);
    bottom.setAlignment(Pos.BOTTOM_RIGHT);

    VBox.setVgrow(top, Priority.ALWAYS);
    card.getChildren().addAll(top, bottom);

    card.setOnMouseClicked(e -> {
      selected = dto;
      message.setText("Seleccionado: " + name + " (" + code + ")");
      message.setVisible(true);
    });
    return card;
  }
}
