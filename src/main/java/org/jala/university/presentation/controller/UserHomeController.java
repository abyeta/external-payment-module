package org.jala.university.presentation.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.application.factory.ServiceFactory;
import org.jala.university.application.service.CustomerService;
import org.jala.university.application.service.ExternalServiceRegistrationService;
import org.jala.university.commons.presentation.BaseController;
import org.jala.university.commons.presentation.ViewSwitcher;
import org.jala.university.presentation.ExternalPaymentView;
import org.jala.university.presentation.GlobalContext;

import java.util.List;
import java.util.UUID;

/**
 * Controller for the User Home view with simple clean UI.
 * Handles service search and linking functionality.
 */
public final class UserHomeController extends BaseController {

  // Mock customer ID for MVP - In production, this would come from authentication
  private static final UUID MOCK_CUSTOMER_ID =
      UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

  private static final int MESSAGE_AUTO_HIDE_DELAY = 3000;
  private static final int ITEM_SPACING = 12;
  private static final int ITEM_HEIGHT = 70;
  private static final int INFO_BOX_SPACING = 4;

  @FXML
  private TextField searchField;

  @FXML
  private Button searchButton;

  @FXML
  private VBox resultsContainer;

  @FXML
  private Label emptyResultsLabel;

  @FXML
  private Label linkedCount;

  @FXML
  private VBox linkedServicesContainer;

  @FXML
  private StackPane overlayPane;

  @FXML
  private VBox confirmBox;

  @FXML
  private Label confirmMessage;

  @FXML
  private Button confirmYes;

  @FXML
  private Button confirmNo;

  private final GlobalContext globalContext = GlobalContext.getInstance();

  private CustomerService linkService;
  private ExternalServiceRegistrationService  externalServiceRegistrationService;
  private UUID pendingServiceId;
  private boolean isLinking;

  @FXML
  public void initialize() {
    linkService = ServiceFactory.getCustomerServiceLinkService();
    externalServiceRegistrationService = ServiceFactory.getRegistrationService();

    // Load linked services on startup
    loadLinkedServices();
  }

  @FXML
  private void backToMenu() {
    ViewSwitcher.switchTo(ExternalPaymentView.START_MENU.getView());
  }

  @FXML
  private void onSearch() {
    String searchTerm = searchField.getText().trim();

    if (searchTerm.isEmpty()) {
      showEmptyState("Please enter a search term");
      return;
    }

    // Clear previous results
    resultsContainer.getChildren().clear();
    showEmptyState("Searching services...");

    // Perform search in background
    new Thread(() -> {
      try {
        List<ExternalServiceDto> results = externalServiceRegistrationService.searchServices(searchTerm);

        Platform.runLater(() -> {
          if (results.isEmpty()) {
            showEmptyState("No services found matching your search");
          } else {
            displaySearchResults(results);
          }
        });
      } catch (Exception e) {
        Platform.runLater(() -> {
          showEmptyState("Error searching services: " + e.getMessage());
          e.printStackTrace();
        });
      }
    }).start();
  }

  @FXML
  private void onConfirmYes() {
    hideConfirmation();
    if (pendingServiceId != null) {
      if (isLinking) {
        performLinkAction(pendingServiceId);
      } else {
        performUnlinkAction(pendingServiceId);
      }
    }
  }

  @FXML
  private void onConfirmNo() {
    hideConfirmation();
    pendingServiceId = null;
  }

  private void showEmptyState(String message) {
    resultsContainer.getChildren().clear();
    emptyResultsLabel.setText(message);
    resultsContainer.getChildren().add(emptyResultsLabel);
  }

  private void displaySearchResults(List<ExternalServiceDto> results) {
    resultsContainer.getChildren().clear();

    for (ExternalServiceDto service : results) {
      HBox item = createSearchResultItem(service);
      resultsContainer.getChildren().add(item);
    }
  }

  private HBox createSearchResultItem(ExternalServiceDto service) {
    HBox item = new HBox(ITEM_SPACING);
    item.setAlignment(Pos.CENTER_LEFT);
    item.setPrefHeight(ITEM_HEIGHT);
    item.setStyle("-fx-background-color: white; "
                + "-fx-border-color: #dee2e6; "
                + "-fx-border-width: 0 0 1 0; "
                + "-fx-padding: 16px;");

    // Icon
    Label icon = new Label("📄");
    icon.setStyle("-fx-font-size: 24px;");

    // Service info
    VBox infoBox = new VBox(INFO_BOX_SPACING);
    HBox.setHgrow(infoBox, Priority.ALWAYS);

    Label nameLabel = new Label(service.getProviderName());
    nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #212529;");

    Label codeLabel = new Label(service.getAccountReference());
    codeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");

    infoBox.getChildren().addAll(nameLabel, codeLabel);

    // Check if already linked
    boolean isLinked = linkService.isServiceLinked(MOCK_CUSTOMER_ID, service.getId());

    if (isLinked) {
      Label linkedLabel = new Label("✓ Already Linked");
      linkedLabel.setStyle("-fx-text-fill: #28a745; -fx-font-size: 13px; -fx-font-weight: bold;");
      item.getChildren().addAll(icon, infoBox, linkedLabel);
    } else {
      Button linkButton = new Button("Link");
      linkButton.setStyle("-fx-background-color: #007bff; "
                        + "-fx-text-fill: white; "
                        + "-fx-font-size: 13px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-background-radius: 4px; "
                        + "-fx-padding: 6 16; "
                        + "-fx-cursor: hand;");

      linkButton.setOnAction(e -> {
        e.consume();
        showConfirmation(service);
      });

      item.getChildren().addAll(icon, infoBox, linkButton);
    }

    return item;
  }

  private void showConfirmation(ExternalServiceDto service) {
    pendingServiceId = service.getId();
    isLinking = true;
    confirmMessage.setText("Link " + service.getProviderName() + " to your account?\n\n"
                          + "You will be able to make payments to this service from your bank account.");
    overlayPane.setVisible(true);
    overlayPane.setManaged(true);
    overlayPane.toFront();
  }

  private void hideConfirmation() {
    overlayPane.setVisible(false);
    overlayPane.setManaged(false);
  }

  private void performLinkAction(UUID serviceId) {
    new Thread(() -> {
      try {
        linkService.linkService(MOCK_CUSTOMER_ID, serviceId);

        Platform.runLater(() -> {
          loadLinkedServices();
          onSearch(); // Refresh search results
        });
      } catch (Exception e) {
        Platform.runLater(() -> {
          showEmptyState("Error linking service: " + e.getMessage());
          e.printStackTrace();
        });
      }
    }).start();
  }

  private void loadLinkedServices() {
    new Thread(() -> {
      try {
        List<ExternalServiceDto> linked = linkService.getLinkedServices(MOCK_CUSTOMER_ID);

        Platform.runLater(() -> {
          linkedCount.setText(String.valueOf(linked.size()));
          displayLinkedServices(linked);
        });
      } catch (Exception e) {
        Platform.runLater(() -> {
          System.err.println("Error loading linked services: " + e.getMessage());
          linkedCount.setText("0");
          displayLinkedServices(List.of());
        });
      }
    }).start();
  }

  private void displayLinkedServices(List<ExternalServiceDto> services) {
    linkedServicesContainer.getChildren().clear();

    if (services.isEmpty()) {
      Label emptyLabel = new Label("You don't have any linked services yet");
      emptyLabel.setStyle("-fx-text-fill: #8b95a1; "
                        + "-fx-font-style: italic; "
                        + "-fx-font-size: 13px; "
                        + "-fx-padding: 16;");
      linkedServicesContainer.getChildren().add(emptyLabel);
      return;
    }

    for (ExternalServiceDto service : services) {
      HBox linkedItem = createLinkedServiceItem(service);
      linkedServicesContainer.getChildren().add(linkedItem);
    }
  }

  private HBox createLinkedServiceItem(ExternalServiceDto service) {
    HBox item = new HBox(ITEM_SPACING);
    item.setAlignment(Pos.CENTER_LEFT);
    item.setPrefHeight(ITEM_HEIGHT);
    item.setStyle("-fx-background-color: white; "
                + "-fx-border-color: #dee2e6; "
                + "-fx-border-width: 1px; "
                + "-fx-border-radius: 6px; "
                + "-fx-background-radius: 6px; "
                + "-fx-padding: 16px;");

    item.setOnMouseClicked(e -> onServiceClicked(service));

    // Icon
    Label icon = new Label("📄");
    icon.setStyle("-fx-font-size: 24px;");

    // Service info
    VBox infoBox = new VBox(INFO_BOX_SPACING);
    HBox.setHgrow(infoBox, Priority.ALWAYS);

    Label nameLabel = new Label(service.getProviderName());
    nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #212529;");

    Label codeLabel = new Label(service.getAccountReference());
    codeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");

    infoBox.getChildren().addAll(nameLabel, codeLabel);

    // Unlink button
    Button unlinkButton = new Button("× Unlink");
    unlinkButton.setStyle("-fx-background-color: white; "
                        + "-fx-text-fill: #dc3545; "
                        + "-fx-font-size: 13px; "
                        + "-fx-font-weight: normal; "
                        + "-fx-border-color: #dc3545; "
                        + "-fx-border-width: 1px; "
                        + "-fx-border-radius: 4px; "
                        + "-fx-background-radius: 4px; "
                        + "-fx-padding: 6 16; "
                        + "-fx-cursor: hand;");

    unlinkButton.setOnAction(e -> {
      e.consume();
      showUnlinkConfirmation(service);
    });

    item.getChildren().addAll(icon, infoBox, unlinkButton);

    return item;
  }

  private void onServiceClicked(ExternalServiceDto externalServiceDto) {
      globalContext.setExternalService(externalServiceDto);
      ViewSwitcher.switchTo(ExternalPaymentView.EXTERNAL_SERVICE.getView());
  }

  private void showUnlinkConfirmation(ExternalServiceDto service) {
    pendingServiceId = service.getId();
    isLinking = false;
    confirmMessage.setText("Unlink " + service.getProviderName() + "?\n\n"
                          + "You will no longer be able to make payments to this service.");
    overlayPane.setVisible(true);
    overlayPane.setManaged(true);
    overlayPane.toFront();
  }

  private void performUnlinkAction(UUID serviceId) {
    new Thread(() -> {
      try {
        linkService.unlinkService(MOCK_CUSTOMER_ID, serviceId);

        Platform.runLater(() -> {
          loadLinkedServices();
          onSearch(); // Refresh search results
        });
      } catch (Exception e) {
        Platform.runLater(() -> {
          showEmptyState("Error unlinking service: " + e.getMessage());
          e.printStackTrace();
        });
      }
    }).start();
  }
}
