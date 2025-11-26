package org.jala.university.presentation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.commons.presentation.BaseController;
import org.jala.university.commons.presentation.ViewSwitcher;
import org.jala.university.infrastructure.external.ApiClientFactory;
import org.jala.university.presentation.ExternalPaymentView;
import org.jala.university.presentation.GlobalContext;

public final class ExternalServiceController extends BaseController {
    @FXML
    public Label feedbackLabel;
    @FXML
    public Button searchButton;
    @FXML
    public Button backButton;
    @FXML
    public Label serviceNameLabel;
    @FXML
    public TextField clientCodeField;

    private ExternalServiceDto externalServiceDto;

    private final GlobalContext globalContext = GlobalContext.getInstance();

    @FXML
    public void initialize() {
        if (globalContext.getExternalService() == null) {
            ExternalServiceDto dto = ExternalServiceDto.builder()
                    .accountReference("1234567890")
                    .providerName("Jala")
                    .build();
            globalContext.setExternalService(dto);
        }
        externalServiceDto = globalContext.getExternalService();
        serviceNameLabel.setText(externalServiceDto.getProviderName());
    }

    @FXML
    public void onBackToMain(ActionEvent actionEvent) {
        ViewSwitcher.switchTo(ExternalPaymentView.USER_HOME.getView());
    }

    @FXML
    public void onSearch(ActionEvent actionEvent) {
        String clientCode = this.clientCodeField.getText();
        if (clientCode == null || clientCode.equals("")) {
            showFeedback("The code user is required");
            return;
        }
        try {
            globalContext.setInvoices(ApiClientFactory.invoices()
                    .getPendingInvoicesByPaymentCode(clientCode).getData());
            globalContext.setUserCode(clientCode);
            ViewSwitcher.switchTo(ExternalPaymentView.EXTERNAL_SERVICE_INVOICES.getView());
        } catch (IllegalArgumentException e) {
            showFeedback("A problem has occurred: " + e.getMessage());
        }
    }

    @FXML
    public void showFeedback(String message) {
        feedbackLabel.setText(message);
        feedbackLabel.setVisible(true);
    }
}
