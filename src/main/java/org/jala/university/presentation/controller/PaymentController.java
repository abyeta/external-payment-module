package org.jala.university.presentation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.jala.university.application.dto.CustomerDto;
import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.application.dto.TransactionDto;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jala.university.application.dto.*;
import org.jala.university.application.factory.ServiceFactory;
import org.jala.university.application.mapper.AccountMapper;
import org.jala.university.application.service.*;
import org.jala.university.commons.presentation.BaseController;
import org.jala.university.commons.presentation.ViewSwitcher;
import org.jala.university.domain.entity.Account;
import org.jala.university.domain.entity.TransactionState;
import org.jala.university.infrastructure.external.ApiClientFactory;
import org.jala.university.infrastructure.external.dto.invoice.InvoiceResponse;
import org.jala.university.presentation.ExternalPaymentView;
import org.jala.university.presentation.GlobalContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class PaymentController extends BaseController {

    @FXML public Label serviceNameLabel;
    @FXML public TextField accountNumberField;
    @FXML public Label feedbackLabel;
    public StackPane rootPane;
    public Button cancelButton;
    public Button payButton;
    public Label amountLabel;

    private ExternalServiceDto  externalService;
    private InvoiceResponse invoice;
    private CustomerDto customer;

    private final AccountService accountService = org.jala.university.ServiceFactory.getAccountService();
    private final TransactionService transactionService = org.jala.university.ServiceFactory.getTransactionService();
    private final GlobalContext globalContext = GlobalContext.getInstance();
    private final AccountMapper accountMapper = new AccountMapper();
    private final PaymentInvoiceService paymentInvoiceService = ServiceFactory.getPaymentInvoiceService();

    private PdfGeneratorService pdfGenerator = null;

    private PdfGeneratorService getPdfGenerator() {
        if (pdfGenerator == null) {
            pdfGenerator = new PdfGeneratorService();
        }
        return pdfGenerator;
    }

    @FXML
    public void initialize() {
        if (globalContext.getExternalService() != null && globalContext.getInvoice() != null) {
            externalService = globalContext.getExternalService();
            customer = globalContext.getCustomer();
            serviceNameLabel.setText(externalService.getProviderName());
            invoice = globalContext.getInvoice();
            amountLabel.setText(String.valueOf(invoice.getAmount()));
        } else {
            throw new RuntimeException("External service or invoice not found");
        }
        accountNumberField.setTextFormatter(createNumericFormatter());
    }

    public void onCancel(ActionEvent actionEvent) {
        ViewSwitcher.switchTo(ExternalPaymentView.EXTERNAL_SERVICE_INVOICES.getView());
    }

    public void onPay(ActionEvent actionEvent) {
        String accountNumber = accountNumberField.getText();
        if (accountNumber == null || accountNumber.isEmpty()) {
            showFeedback("Please enter a valid account number");
            return;
        }

        Long serviceAccountNumber = externalService.getAccountNumber();
        Long userAccountNumber = Long.valueOf(accountNumberField.getText());

        try {
            Account userAccount = accountMapper.mapFrom(accountService.findByAccountNumber(userAccountNumber));
            Account serviceAccount = accountMapper.mapFrom(accountService.findByAccountNumber(serviceAccountNumber));
            if (!accountService.validateSufficientBalance(userAccountNumber, invoice.getAmount().doubleValue())) {
                showFeedback("Insufficient balance");
                return;
            }

            TransactionDto transactionDto = TransactionDto.builder()
                    .sourceAccount(userAccount)
                    .destinationAccount(serviceAccount)
                    .amount(invoice.getAmount().doubleValue())
                    .date(LocalDateTime.now())
                    .state(TransactionState.COMPLETED)
                    .build();

            TransactionDto dto = transactionService.createTransaction(transactionDto);
            if (dto != null && dto.getState() == TransactionState.COMPLETED) {

                PaymentInvoiceDto paymentInvoiceDto = PaymentInvoiceDto.builder()
                        .externalServiceId(externalService.getId())
                        .customerId(customer.getId())
                        .amount(invoice.getAmount().doubleValue())
                        .serviceName(externalService.getProviderName())
                        .serviceNumberReference(externalService.getAccountReference())
                        .serviceEmail(externalService.getEmail() != null ? externalService.getEmail() : "")
                        .paymentDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                        .build();

                PaymentInvoiceDto savedInvoice = paymentInvoiceService.savePaymentInvoice(paymentInvoiceDto);

                // dto con todos los campos
                savedInvoice.setServiceName(externalService.getProviderName());
                savedInvoice.setServiceNumberReference(externalService.getAccountReference());
                savedInvoice.setServiceEmail(externalService.getEmail() != null ? externalService.getEmail() : "");
                savedInvoice.setPaymentDate(LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

                globalContext.setPaymentInvoice(savedInvoice);
                showPopUp();
                ApiClientFactory.invoices().payInvoice(invoice.getInvoiceNumber());
                globalContext.getInvoices().remove(invoice);
            } else {
                System.out.println("transaction not created");
            }
        } catch (IllegalArgumentException e) {
            showFeedback("A problem has occurred: " + e.getMessage());
        }
    }

    public TextFormatter<String> createNumericFormatter() {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        });
    }

    public void showFeedback(String feedback) {
        feedbackLabel.setText(feedback);
        feedbackLabel.setVisible(true);
    }

    public void showPopUp() {
        VBox popUpVBox = createPopUpVBox();
        rootPane.getChildren().add(popUpVBox);
        accountNumberField.setEditable(false);
        cancelButton.setDisable(true);
        payButton.setDisable(true);
    }

    public VBox createPopUpVBox() {
        VBox popUpVBox = new VBox();
        popUpVBox.setStyle("-fx-padding: 20 20 20 20; -fx-background-color: white;"
                + "-fx-background-radius: 12px;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 10, 0, 0, 2);"
                + "-fx-max-width: 350;"
                + "-fx-max-height: 200;");
        VBox contentBox = createContentBox();
        popUpVBox.getChildren().add(contentBox);
        return popUpVBox;
    }

    public VBox createContentBox() {
        VBox contentBox = new VBox();
        contentBox.setStyle("-fx-padding: 10 10 10 10; -fx-background-color: white;"
                + "-fx-background-radius: 12px;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 10, 0, 0, 2);");
        VBox.setVgrow(contentBox, Priority.ALWAYS);
        VBox exitBox = createExitBox();
        VBox textBox = createTextBox();
        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setStyle("-fx-padding: 10 0 10 0");
        HBox actionButtonBox = createActionButtonBox();
        contentBox.getChildren().addAll(exitBox, textBox, separator, actionButtonBox);
        return contentBox;
    }

    public VBox createTextBox() {
        VBox textBox = new VBox();
        textBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(textBox, Priority.ALWAYS);
        Label messageLabel = new Label("Payment successful");
        messageLabel.setStyle("-fx-font-size: 15px; -fx-font-family: 'Aptos Black'");
        textBox.getChildren().addAll(messageLabel);
        return textBox;
    }

    public HBox createActionButtonBox() {
        final int spacing = 10;
        HBox actionButtonBox = new HBox();
        actionButtonBox.setSpacing(spacing);
        actionButtonBox.setAlignment(Pos.CENTER_RIGHT);
        Button viewButton = new Button("View invoice");
        viewButton.setStyle("-fx-background-color: rgba(136,244,253,0.72); "
                + "-fx-min-width: 80px; "
                + "-fx-background-radius: 6px; "
                + "-fx-min-height: 40px;"
                + "-fx-font-size: 13px; ");
        viewButton.setOnAction(event -> onViewInvoice());

        Button downloadButton = new Button("Download");
        downloadButton.setOnAction(event -> onDownload());
        downloadButton.setStyle("-fx-background-color: rgba(105,227,139,0.97); "
                + "-fx-min-width: 80px; "
                + "-fx-background-radius: 6px; "
                + "-fx-min-height: 40px;"
                + "-fx-font-size: 13px; ");
        actionButtonBox.getChildren().addAll(viewButton, downloadButton);
        return actionButtonBox;
    }

    public VBox createExitBox() {
        VBox exitBox = new VBox();
        exitBox.setAlignment(Pos.CENTER_LEFT);
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: rgb(228,93,93); "
                + "-fx-min-width: 60px; "
                + "-fx-background-radius: 6px; "
                + "-fx-min-height: 40px;"
                + "-fx-font-size: 13px; ");
        exitButton.setOnAction(event -> onExit());
        exitBox.getChildren().addAll(exitButton);
        return exitBox;
    }

    public void onExit() {
        ViewSwitcher.switchTo(ExternalPaymentView.EXTERNAL_SERVICE_INVOICES.getView());
    }

    public void onViewInvoice() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().
                    getResource(ExternalPaymentView.PAYMENT_INVOICE.getView().getFileName()));
            Parent view = loader.load();
            Stage newWindow = new Stage();
            newWindow.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(view);
            newWindow.setScene(scene);
            newWindow.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onDownload() {
        PaymentInvoiceDto dto = GlobalContext.getInstance().getPaymentInvoice();

        if (dto == null || dto.getId() == null) {
            new Alert(Alert.AlertType.WARNING, "No hay comprobante válido para descargar").showAndWait();
            return;
        }

        try {
            byte[] pdfBytes = getPdfGenerator().generarComprobante(dto.getId());

            String fileName = String.format("Comprobante_%s_%s.pdf",
                    dto.getServiceName().replaceAll("[^a-zA-Z0-9_-]", "_"),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            );

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar comprobante de pago");
            fileChooser.setInitialFileName(fileName);
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));

            File archivo = fileChooser.showSaveDialog(null);
            if (archivo != null) {
                Files.write(archivo.toPath(), pdfBytes);
                new Alert(Alert.AlertType.INFORMATION,
                        "¡Comprobante descargado correctamente!\n" + archivo.getName()).showAndWait();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Error al generar el PDF:\n" + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }
}
