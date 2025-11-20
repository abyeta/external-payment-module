package org.jala.university.presentation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.application.dto.InvoiceDto;
import org.jala.university.commons.presentation.BaseController;
import org.jala.university.commons.presentation.ViewSwitcher;
import org.jala.university.presentation.ExternalPaymentView;
import org.jala.university.presentation.GlobalContext;

import java.util.List;

public final class ExternalServiceInvoicesController extends BaseController {
    private static final int MIN_WIDTH = 150;
    private static final int MIN_CODE_WIDTH = 200;

    public VBox invoicesTableContainer;
    public ScrollPane invoicesScrollPane;
    public Button backButton;
    public Label serviceNameLabel;

    private ExternalServiceDto externalService;
    private List<InvoiceDto>  invoices;

    private final GlobalContext globalContext =  GlobalContext.getInstance();

    @FXML
    public void initialize() {

        invoices = globalContext.getInvoices();
        externalService = globalContext.getExternalService();
        serviceNameLabel.setText(externalService.getProviderName());
        setInvoices();
    }

    public void setInvoices() {

        if (invoices == null || invoices.isEmpty()) {
            Label emptyLabel = new Label("There are not invoices to process");
            emptyLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 14px; -fx-padding: 40; -fx-alignment: center;");
            invoicesTableContainer.setAlignment(Pos.CENTER);
            invoicesTableContainer.getChildren().add(emptyLabel);
            return;
        }

        for (InvoiceDto invoice : invoices) {
            HBox sectionBox = setInvoiceBox(invoice);
            invoicesTableContainer.getChildren().add(sectionBox);
        }

    }

    public HBox setInvoiceBox(InvoiceDto dto) {
        HBox invoiceBox =  new HBox();
        invoiceBox.setAlignment(Pos.CENTER_LEFT);
        invoiceBox.setStyle("-fx-background-color: white; "
                + "-fx-padding: 10 0 10 0; "
                + "-fx-border-color: #e0e0e0; "
                + "-fx-border-width: 0 0 1 0;");
        invoiceBox.setOnMouseEntered(event -> invoiceBox.setStyle("-fx-background-color: rgba(122,198,195,0.47); "
                + "-fx-padding: 10 0 10 0; "
                + "-fx-border-color: rgba(5,207,201,0.47); "
                + "-fx-border-width: 0 0 1 0;"));
        invoiceBox.setOnMouseExited(event -> invoiceBox.setStyle("-fx-background-color: white; "
                + "-fx-padding: 10 0 10 0; "
                + "-fx-border-color: #e0e0e0; "
                + "-fx-border-width: 0 0 1 0;"));

        //TODO the service type was hardcoded. After implementation of API verify if is a possible field.
        Label serviceTypeLabel = new Label("Example Service Type");
        serviceTypeLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(serviceTypeLabel, Priority.ALWAYS);
        Label codeLabel = createColumnLabel(dto.getCode(), MIN_CODE_WIDTH);
        Label statusLabel = createColumnLabel(dto.getStatus(), MIN_WIDTH);
        Label amountLabel = createColumnLabel(String.valueOf(dto.getAmount()), MIN_WIDTH);
        HBox actionBox = createActionButtons(dto);
        invoiceBox.getChildren().addAll(serviceTypeLabel, codeLabel, statusLabel, amountLabel, actionBox);

        return invoiceBox;
    }

    //Create the action buttons for every invoice
    public HBox createActionButtons(InvoiceDto dto) {
        final int spacing = 6;
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.TOP_CENTER);
        buttonBox.setMinWidth(MIN_WIDTH);
        buttonBox.setSpacing(spacing);

        Button payButton = createButton("Pay", "rgba(100,243,87,");
        payButton.setOnAction(event -> onPay(dto));
        Button viewButton = createButton("View", "rgb(35,142,243,");
        viewButton.setOnAction(event -> onView(dto));
        buttonBox.getChildren().addAll(payButton, viewButton);
        return buttonBox;
    }

    public Button createButton(String buttonText, String color) {
        Button button = new Button(buttonText);
        button.setStyle(
                "-fx-background-color: " + color + "0.7); "
                        + "-fx-font-family: 'System Bold';"
                        + "-fx-font-size: 11;");
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: " + color + "1.5); "
                + "-fx-font-family: 'System Bold';"
                + "-fx-font-size: 11;"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: " + color + "0.7); "
                + "-fx-font-family: 'System Bold';"
                + "-fx-font-size: 11;"));
        return button;
    }

    public void onView(InvoiceDto dto) {
        globalContext.setInvoice(dto);
        ViewSwitcher.switchTo(ExternalPaymentView.INVOICE_DETAILS.getView());
    }

    public void onPay(InvoiceDto invoiceDto) {
        globalContext.setInvoice(invoiceDto);
        ViewSwitcher.switchTo(ExternalPaymentView.PAYMENT.getView());
    }

    public Label createColumnLabel(String text, int size) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #6c757d;"
               + "    -fx-font-size: 11px;"
               + "    -fx-font-weight: bold;"
               + "    -fx-min-width: " + size + "px;"
               + "    -fx-pref-width: " + size + "px;"
               + "    -fx-max-width: " + size + "px;"
               + "    -fx-alignment: CENTER_LEFT;");
        return label;
    }

    public void onBackExternalService(ActionEvent actionEvent) {
        ViewSwitcher.switchTo(ExternalPaymentView.EXTERNAL_SERVICE.getView());
    }
}
