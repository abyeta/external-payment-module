package org.jala.university.presentation.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.application.dto.InvoiceDto;
import org.jala.university.application.dto.TransactionDto;
import org.jala.university.application.mapper.AccountMapper;
import org.jala.university.application.mapper.InvoiceMapper;
import org.jala.university.application.mapper.TransactionMapper;
import org.jala.university.application.service.*;
import org.jala.university.commons.presentation.BaseController;
import org.jala.university.commons.presentation.ViewSwitcher;
import org.jala.university.domain.entity.TransactionState;
import org.jala.university.infrastructure.api.ServicesAPI;
import org.jala.university.infrastructure.persistance.AccountRepositoryImpl;
import org.jala.university.infrastructure.persistance.TransactionRepositoryImpl;
import org.jala.university.presentation.ExternalPaymentView;
import org.jala.university.presentation.GlobalContext;

import java.time.LocalDateTime;

public class PaymentController extends BaseController {

    @FXML public Label serviceNameLabel;
    @FXML public TextField accountNumberField;
    @FXML public Label feedbackLabel;
    public StackPane rootPane;
    public Button cancelButton;
    public Button payButton;
    public Label amountLabel;

    private ExternalServiceDto  externalService;
    private InvoiceDto invoice;
    private TransactionService transactionService;
    private AccountService accountService;

    private final GlobalContext globalContext = GlobalContext.getInstance();
    private final ExternalApiService service = new ExternalApiServiceImpl(new ServicesAPI(), new InvoiceMapper());


    @FXML
    public void initialize() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();
        accountService = new AccountServiceImpl(new AccountMapper(), new AccountRepositoryImpl(em));
        transactionService = new TransactionServiceImpl(
                new TransactionMapper(),
                new TransactionRepositoryImpl(em),
                new AccountRepositoryImpl(em),
                accountService);

        if(globalContext.getExternalService() != null && globalContext.getInvoice()!=null) {
            externalService = globalContext.getExternalService();
            serviceNameLabel.setText(externalService.getProviderName());
            invoice = globalContext.getInvoice();
        } else {
            throw  new RuntimeException("External service or invoice not found");
        }
        amountLabel.setText(String.valueOf(invoice.getAmount()));
        accountNumberField.setTextFormatter(createNumericFormatter());

    }

    public void onCancel(ActionEvent actionEvent) {
        ViewSwitcher.switchTo(ExternalPaymentView.EXTERNAL_SERVICE_INVOICES.getView());
    }

    public void onPay(ActionEvent actionEvent) {
        String accountNumber = accountNumberField.getText();
        if(accountNumber == null || accountNumber.isEmpty()) {
            showFeedback("Please enter a valid account number");
            return;
        }

        Long serviceAccountNumber = externalService.getAccountNumber();
        Long userAccountNumber = Long.valueOf(accountNumberField.getText());
        try {
            if(!accountService.validateSufficientBalance(userAccountNumber, invoice.getAmount())){
                showFeedback("Insufficient balance");
                return;
            }

            TransactionDto transactionDto = TransactionDto.builder()
                    .source(accountNumber)
                    .destination(serviceAccountNumber.toString())
                    .amount(invoice.getAmount())
                    .date(LocalDateTime.now())
                    .state(TransactionState.COMPLETED)
                    .build();

            TransactionDto dto = transactionService.createTransaction(transactionDto);
            if(dto != null && dto.getState() == TransactionState.COMPLETED) {
                showPopUp();
                service.updateStatus(invoice.getCode(), externalService.getAccountReference());
                globalContext.getInvoices().remove(invoice);
            } else
            {
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

    public VBox createTextBox () {
        VBox textBox = new VBox();
        textBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(textBox, Priority.ALWAYS);
        Label messageLabel = new Label("Payment successful");
        messageLabel.setStyle("-fx-font-size: 15px; -fx-font-family: 'Aptos Black'");
        textBox.getChildren().addAll(messageLabel);
        return textBox;
    }

    public HBox createActionButtonBox() {
        HBox actionButtonBox = new HBox();
        actionButtonBox.setSpacing(10);
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
        //TODO here will go the change to view invoice view
    }

    public void onDownload() {
        //TODO here will go the download process
    }
}
