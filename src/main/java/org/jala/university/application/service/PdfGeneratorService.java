package org.jala.university.application.service;

import jakarta.persistence.Persistence;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.jala.university.domain.entity.*;
import org.jala.university.domain.repository.PaymentInvoiceRepository;
import org.jala.university.infrastructure.persistance.PaymentInvoiceRepositoryImpl;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;


public final class PdfGeneratorService {

    private final PaymentInvoiceRepository invoiceRepository; // constructor alternativo para inyectar mock

    public PdfGeneratorService(PaymentInvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public PdfGeneratorService() {
        var em = Persistence.createEntityManagerFactory("external-payment-pu").createEntityManager();
        this.invoiceRepository = new PaymentInvoiceRepositoryImpl(em);
    }

    private static final float MARGIN_LEFT = 50f;
    private static final float HEADER_Y = 750f;
    private static final float TITLE_Y = 700f;
    private static final float START_Y = 650f;
    private static final float LINE_SPACING = 20f;
    private static final float DESCRIPTION_SPACING = 30f;
    private static final float DESCRIPTION_LINE_GAP = 18f;
    private static final float DESCRIPTION_INDENT = 20f;
    private static final float FOOTER_Y = 50f;
    private static final int MAGIC_1000 = 1000;
    private static final int MAGIC_2 = 2;
    private static final float FONT_SIZE_HEADER = 14f;
    private static final float FONT_SIZE_TITLE = 18f;
    private static final float FONT_SIZE_BODY = 12f;
    private static final float FONT_SIZE_FOOTER = 10f;

    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final int CODE_SHORT_LENGTH = 4;

    public byte[] generarComprobante(UUID invoiceId) throws Exception {

        Optional<PaymentInvoice> optionalInvoice = Optional.ofNullable(invoiceRepository.findById(invoiceId));
        if (optionalInvoice.isEmpty()) {
            throw new RuntimeException("No se encontró el pago con ID: " + invoiceId);
        }

        PaymentInvoice invoice = optionalInvoice.get();
        Customer customer = invoice.getCustomer();
        ExternalService service = invoice.getExternalService();

        String codigo = "INV-" + invoice.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + invoiceId.toString().substring(0, CODE_SHORT_LENGTH).toUpperCase();

        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(doc, page)) {

                // ENCABEZADO
                content.beginText();
                content.setFont(PDType1Font.COURIER_BOLD, FONT_SIZE_HEADER);
                content.newLineAtOffset(MARGIN_LEFT, HEADER_Y);
                content.showText("JALA UNIVERSITY");
                content.endText();

                // TÍTULO
                content.beginText();
                content.setFont(PDType1Font.COURIER_BOLD, FONT_SIZE_TITLE);
                content.newLineAtOffset(MARGIN_LEFT, TITLE_Y);
                content.showText("COMPROBANTE DE PAGO");
                content.endText();

                content.beginText();
                content.setFont(PDType1Font.COURIER, FONT_SIZE_BODY);
                content.newLineAtOffset(MARGIN_LEFT, START_Y);

                content.showText("Código: " + codigo);
                content.newLineAtOffset(0, -LINE_SPACING);
                content.showText("Cliente: " + customer.getName());
                content.newLineAtOffset(0, -LINE_SPACING);
                content.showText("Monto: USD " + String.format(Locale.US, "%,.2f", invoice.getAmount()));
                content.newLineAtOffset(0, -LINE_SPACING);
                content.showText("Estado: PAGADO");
                content.newLineAtOffset(0, -LINE_SPACING);
                content.showText("Fecha de pago: " + invoice.getPaymentDate().format(DISPLAY_DATE));
                content.newLineAtOffset(0, -LINE_SPACING * 2);
                content.showText("Detalles del servicio:");
                content.newLineAtOffset(DESCRIPTION_INDENT, -DESCRIPTION_LINE_GAP);
                content.showText(service.getProviderName());
                content.newLineAtOffset(0, -DESCRIPTION_LINE_GAP);
                content.showText("Referencia: " + service.getAccountReference());
                content.newLineAtOffset(0, -DESCRIPTION_LINE_GAP);
                content.showText("Teléfono: " + service.getPhoneCountryCode() + " " + service.getPhoneNumber());

                // Footer centrado
                content.endText();
                content.beginText();
                content.setFont(PDType1Font.COURIER, FONT_SIZE_FOOTER);
                String footer = "Gracias por confiar en Jala University © 2025";
                float footerWidth = PDType1Font.COURIER.getStringWidth(footer) / MAGIC_1000 * FONT_SIZE_FOOTER;
                float xCenter = (page.getMediaBox().getWidth() - footerWidth) / MAGIC_2;
                content.newLineAtOffset(xCenter, FOOTER_Y);
                content.showText(footer);
                content.endText();
            }

            doc.save(out);
            return out.toByteArray();
        }

    }

}
