package org.jala.university.presentation;

import lombok.Getter;
import org.jala.university.commons.presentation.View;

@Getter
public enum ExternalPaymentView {
    START_MENU("external-service-start-view.fxml"),
    MAIN("main-menu.fxml"),
    EXTERNAL_SERVICE_REGISTRATION("external-service-registration-view.fxml"),
    EXTERNAL_SERVICE("external-service-view.fxml"),
    EXTERNAL_SERVICE_INVOICES("external-service-invoices-view.fxml"),
    INVOICE_DETAILS("invoice-details-view.fxml"),
    USER_HOME("user-home-view.fxml");

    private final View view;

    ExternalPaymentView(String fileName) {
        this.view = new View(fileName);
    }

}
