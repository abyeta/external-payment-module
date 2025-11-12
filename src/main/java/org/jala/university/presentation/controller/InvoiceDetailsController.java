package org.jala.university.presentation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import org.jala.university.application.dto.InvoiceDto;
import org.jala.university.commons.presentation.BaseController;
import org.jala.university.commons.presentation.ViewSwitcher;
import org.jala.university.presentation.ExternalPaymentView;
import org.jala.university.presentation.GlobalContext;

public final class InvoiceDetailsController extends BaseController {
    public Button backButton;
    public Label userNameField;
    public Label userCodeField;
    public Label billingPeriodLabel;
    public Label invoiceCodeLabel;
    public Text descriptionText;
    public Button payButton;
    public Label amountLabel;
    public Label expirationLabel;
    public Label serviceNameLabel;

    private InvoiceDto invoice;
    private final GlobalContext globalContext = GlobalContext.getInstance();

    @FXML
    public void initialize() {
        invoice = globalContext.getInvoice();
        renderInvoice();
    }

    private void renderInvoice() {

        if (invoice == null) {
            return;
        }

        userNameField.setText(invoice.getClientName());
        userCodeField.setText(globalContext.getUserCode());
        invoiceCodeLabel.setText(invoice.getCode());
        expirationLabel.setText(invoice.getExpirationDate());
        amountLabel.setText(String.valueOf(invoice.getAmount()));
        descriptionText.setText(invoice.getDescription());
        serviceNameLabel.setText(globalContext.getExternalService().getProviderName());

    }

    public void onBackToInvoices(ActionEvent actionEvent) {
        ViewSwitcher.switchTo(ExternalPaymentView.EXTERNAL_SERVICE_INVOICES.getView());
    }

    public void onPayment(ActionEvent actionEvent) {
        globalContext.setInvoice(invoice);
        ViewSwitcher.switchTo(ExternalPaymentView.PAYMENT.getView());
    }
}
