package org.jala.university.presentation.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jala.university.application.dto.PaymentInvoiceDto;
import org.jala.university.application.service.PdfGeneratorService;
import org.jala.university.commons.presentation.BaseController;
import org.jala.university.commons.presentation.ViewSwitcher;
import org.jala.university.presentation.ExternalPaymentView;
import org.jala.university.presentation.GlobalContext;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.jala.university.application.factory.ServiceFactory;

public final class PaymentInvoiceController extends BaseController {

    @FXML public Label serviceNameLabel;
    @FXML public Label serviceNumberReferenceLabel;
    @FXML public Label serviceEmailLabel;
    @FXML public Label paymentDateField;
    @FXML public Label amountLabel;

    private PaymentInvoiceDto paymentInvoiceDto;
    private final GlobalContext globalContext = GlobalContext.getInstance();
    private PdfGeneratorService pdfGenerator;
    private PaymentInvoiceDto paymentInvoiceFull;

    private PdfGeneratorService getPdfGenerator() {
        if (pdfGenerator == null) {
            pdfGenerator = new PdfGeneratorService();
        }
        return pdfGenerator;
    }

    @FXML
    public void initialize() {
        paymentInvoiceDto = globalContext.getPaymentInvoice();

        if (paymentInvoiceDto == null || paymentInvoiceDto.getId() == null) {
            showErrorAndGoHome("Error crítico: no se encontró el comprobante.");
            return;
        }

        // DTO completo desde el ServiceFactory (incluye pdfContent)D
        paymentInvoiceFull = ServiceFactory.getPaymentInvoiceService()
                .findById(paymentInvoiceDto.getId());
// después borrar lo de abajo
        if (paymentInvoiceFull == null) {
            showErrorAndGoHome("Error: no se pudo cargar el detalle del comprobante.");
            return;
        }

        chargeData();
    }

    private void chargeData() {
        if (paymentInvoiceDto.getServiceName() != null) {
            serviceNameLabel.setText(paymentInvoiceDto.getServiceName());
        }
        if (paymentInvoiceDto.getServiceNumberReference() != null) {
            serviceNumberReferenceLabel.setText(paymentInvoiceDto.getServiceNumberReference());
        }
        if (paymentInvoiceDto.getServiceEmail() != null) {
            serviceEmailLabel.setText(paymentInvoiceDto.getServiceEmail());
        }
        if (paymentInvoiceDto.getPaymentDate() != null) {
            paymentDateField.setText(paymentInvoiceDto.getPaymentDate());
        }
        if (paymentInvoiceDto.getAmount() != null) {
            amountLabel.setText(String.format("%.2f", paymentInvoiceDto.getAmount()));
        }
    }

    @FXML
    public void onExit(ActionEvent actionEvent) {
        ((Stage) amountLabel.getScene().getWindow()).close();
    }

    @FXML
    public void onDownload(ActionEvent actionEvent) {
        if (paymentInvoiceDto == null || paymentInvoiceDto.getId() == null) {
            new Alert(Alert.AlertType.WARNING, "No hay comprobante válido para descargar.").showAndWait();
            return;
        }

        try {
            byte[] pdfBytes;

            // si el pdf ya está generado lo usamos directo (instantáneo)
            if (paymentInvoiceFull.getPdfContent() != null && paymentInvoiceFull.getPdfContent().length > 0) {
                pdfBytes = paymentInvoiceFull.getPdfContent();
            } else {
                // SI NO → lo generamos una sola vez y lo guardamos
                pdfBytes = getPdfGenerator().generarComprobante(paymentInvoiceDto.getId());
                paymentInvoiceFull.setPdfContent(pdfBytes);

                // Guardamos solo el PDF (el resto del objeto ya existe en BD)
                ServiceFactory.getPaymentInvoiceService()
                        .savePaymentInvoice(paymentInvoiceFull);
            }
            String safeName = paymentInvoiceDto.getServiceName()
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

            File archivo = fileChooser.showSaveDialog(null);
            if (archivo != null) {
                Files.write(archivo.toPath(), pdfBytes);
                new Alert(Alert.AlertType.INFORMATION,
                        "¡Comprobante descargado correctamente!\n" + archivo.getName())
                        .showAndWait();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo generar el PDF:\n" + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }

    private void showErrorAndGoHome(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
        ViewSwitcher.switchTo(ExternalPaymentView.USER_HOME.getView());
    }
}
