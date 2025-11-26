package org.jala.university.presentation.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jala.university.application.dto.PaymentInvoiceDto;
import org.jala.university.application.factory.ServiceFactory;
import org.jala.university.application.service.PaymentInvoiceService;
import org.jala.university.commons.presentation.BaseController;
import org.jala.university.commons.presentation.ViewSwitcher;
import org.jala.university.presentation.ExternalPaymentView;
import org.jala.university.presentation.GlobalContext;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.jala.university.application.service.PdfGeneratorService;

import java.util.List;
import java.util.UUID;

public final class PaymentHistoryController extends BaseController {
    private static final int MIN_WIDTH = 200;
    private static final int MIN_DATE_WIDTH = 180;
    private static final int PAYMENT_BOX_SPACING = 10;
    private static final int MESSAGE_DISPLAY_TIME = 3000;

    @FXML
    public VBox paymentsTableContainer;
    @FXML
    public ScrollPane paymentsScrollPane;
    @FXML
    public Button backButton;

    private final GlobalContext globalContext = GlobalContext.getInstance();
    private final PaymentInvoiceService paymentInvoiceService = ServiceFactory.getPaymentInvoiceService();
    private List<PaymentInvoiceDto> allPayments;

    @FXML
    public void initialize() {
        loadPayments();
    }

    private void loadPayments() {
        new Thread(() -> {
            try {
                // Get client code from global context or use a default one
                String clientCode = globalContext.getUserCode();
                if (clientCode == null || clientCode.isEmpty()) {
                    clientCode = "123456"; // Default code for testing
                }

                Long clientId = globalContext.getCustomer().getId();
                UUID serviceId = globalContext.getExternalService().getId();

                allPayments = paymentInvoiceService.findByCustomerAndService(clientId, serviceId);

                Platform.runLater(() -> {
                    displayPayments();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showEmptyState("Error loading payment history: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void displayPayments() {
        paymentsTableContainer.getChildren().clear();

        if (allPayments == null || allPayments.isEmpty()) {
            showEmptyState("No payments registered");
            return;
        }

        for (PaymentInvoiceDto payment : allPayments) {
            HBox paymentBox = createPaymentBox(payment);
            paymentsTableContainer.getChildren().add(paymentBox);
        }
    }

    private HBox createPaymentBox(PaymentInvoiceDto payment) {
        HBox paymentBox = new HBox();
        paymentBox.setAlignment(Pos.CENTER_LEFT);
        paymentBox.setSpacing(PAYMENT_BOX_SPACING);
        paymentBox.setStyle("-fx-background-color: white; "
                + "-fx-padding: 15 0 15 0; "
                + "-fx-border-color: #e0e0e0; "
                + "-fx-border-width: 0 0 1 0;");

        paymentBox.setOnMouseEntered(event -> paymentBox.setStyle("-fx-background-color: rgba(122,198,195,0.47); "
                + "-fx-padding: 15 0 15 0; "
                + "-fx-border-color: rgba(5,207,201,0.47); "
                + "-fx-border-width: 0 0 1 0;"));
        paymentBox.setOnMouseExited(event -> paymentBox.setStyle("-fx-background-color: white; "
                + "-fx-padding: 15 0 15 0; "
                + "-fx-border-color: #e0e0e0; "
                + "-fx-border-width: 0 0 1 0;"));

        // Date
        Label dateLabel = createColumnLabel(payment.getPaymentDate().toString(), MIN_DATE_WIDTH);

        // Service/Provider - using the invoice description
        Label serviceLabel = new Label(payment.getServiceName());
        serviceLabel.setStyle("-fx-text-fill: #212529; -fx-font-size: 13px;");
        serviceLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(serviceLabel, Priority.ALWAYS);

        // Amount
        Label amountLabel = createColumnLabel("$ " + String.format("%.0f", payment.getAmount()), MIN_WIDTH);
        amountLabel.setStyle("-fx-text-fill: #212529; -fx-font-size: 14px; -fx-font-weight: bold; "
                + "-fx-min-width: " + MIN_WIDTH + "px; "
                + "-fx-pref-width: " + MIN_WIDTH + "px; "
                + "-fx-max-width: " + MIN_WIDTH + "px; "
                + "-fx-alignment: CENTER_LEFT;");

        // Download button
        HBox actionBox = createActionButton(payment);

        paymentBox.getChildren().addAll(dateLabel, serviceLabel, amountLabel, actionBox);

        return paymentBox;
    }

    private HBox createActionButton(PaymentInvoiceDto payment) {
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMinWidth(MIN_WIDTH);

        Button downloadButton = new Button("Download receipt");
        downloadButton.setStyle(
                "-fx-background-color: white; "
                        + "-fx-text-fill: #007bff; "
                        + "-fx-border-color: #007bff; "
                        + "-fx-border-width: 1px; "
                        + "-fx-border-radius: 4px; "
                        + "-fx-background-radius: 4px; "
                        + "-fx-font-size: 12px; "
                        + "-fx-padding: 6 12; "
                        + "-fx-cursor: hand;");

        downloadButton.setOnMouseEntered(event -> downloadButton.setStyle(
                "-fx-background-color: #007bff; "
                        + "-fx-text-fill: white; "
                        + "-fx-border-color: #007bff; "
                        + "-fx-border-width: 1px; "
                        + "-fx-border-radius: 4px; "
                        + "-fx-background-radius: 4px; "
                        + "-fx-font-size: 12px; "
                        + "-fx-padding: 6 12; "
                        + "-fx-cursor: hand;"));

        downloadButton.setOnMouseExited(event -> downloadButton.setStyle(
                "-fx-background-color: white; "
                        + "-fx-text-fill: #007bff; "
                        + "-fx-border-color: #007bff; "
                        + "-fx-border-width: 1px; "
                        + "-fx-border-radius: 4px; "
                        + "-fx-background-radius: 4px; "
                        + "-fx-font-size: 12px; "
                        + "-fx-padding: 6 12; "
                        + "-fx-cursor: hand;"));

        downloadButton.setOnAction(event -> onDownloadReceipt(payment));
        buttonBox.getChildren().add(downloadButton);

        return buttonBox;
    }

    private void onDownloadReceipt(PaymentInvoiceDto payment) {
        if (payment == null || payment.getId() == null) {
            Platform.runLater(() -> new Alert(Alert.AlertType.WARNING,
                    "No hay comprobante válido para descargar.").showAndWait());
            return;
        }

        try {
            byte[] pdfBytes;

            PaymentInvoiceDto paymentFull = ServiceFactory.getPaymentInvoiceService()
                    .findById(payment.getId());

            if (paymentFull != null && paymentFull.getPdfContent() != null && paymentFull.getPdfContent().length > 0) {
                pdfBytes = paymentFull.getPdfContent();
                System.out.println("Encontrado");

            } else {
                pdfBytes = new PdfGeneratorService().generarComprobante(payment.getId());
                if (paymentFull != null) {
                    paymentFull.setPdfContent(pdfBytes);
                    ServiceFactory.getPaymentInvoiceService().savePaymentInvoice(paymentFull);
                }
            }

            String safeName = payment.getServiceName()
                    .replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = String.format("Comprobante_%s_%s.pdf",
                    safeName,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            );

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar comprobante de pago");
            fileChooser.setInitialFileName(fileName);
            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));

            File archivo = fileChooser.showSaveDialog(paymentsScrollPane.getScene().getWindow());
            if (archivo != null) {
                Files.write(archivo.toPath(), pdfBytes);
                showTemporaryMessage("¡Comprobante descargado correctamente!");
            }

        } catch (Exception e) {
            showTemporaryMessage("Error al descargar el comprobante");
            e.printStackTrace();
        }
    }

    private void showTemporaryMessage(String message) {
        // Create a temporary label to display the message
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-background-color: #d4edda; "
                + "-fx-text-fill: #155724; "
                + "-fx-padding: 10; "
                + "-fx-border-color: #c3e6cb; "
                + "-fx-border-width: 1; "
                + "-fx-border-radius: 4; "
                + "-fx-background-radius: 4;");

        paymentsTableContainer.getChildren().add(0, messageLabel);

        // Remove after a few seconds
        new Thread(() -> {
            try {
                Thread.sleep(MESSAGE_DISPLAY_TIME);
                Platform.runLater(() -> paymentsTableContainer.getChildren().remove(messageLabel));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private Label createColumnLabel(String text, int size) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #6c757d; "
                + "-fx-font-size: 13px; "
                + "-fx-min-width: " + size + "px; "
                + "-fx-pref-width: " + size + "px; "
                + "-fx-max-width: " + size + "px; "
                + "-fx-alignment: CENTER_LEFT;");
        return label;
    }

    private void showEmptyState(String message) {
        paymentsTableContainer.getChildren().clear();
        Label emptyLabel = new Label(message);
        emptyLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 14px; -fx-padding: 40; -fx-alignment: center;");
        paymentsTableContainer.setAlignment(Pos.CENTER);
        paymentsTableContainer.getChildren().add(emptyLabel);
    }

    @FXML
    public void onBackToMenu(ActionEvent actionEvent) {
        ViewSwitcher.switchTo(ExternalPaymentView.USER_HOME.getView());
    }
}
