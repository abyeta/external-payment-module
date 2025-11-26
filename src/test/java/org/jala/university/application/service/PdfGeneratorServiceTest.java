package org.jala.university.application.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jala.university.domain.entity.Customer;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.entity.PaymentInvoice;
import org.jala.university.domain.repository.PaymentInvoiceRepository;
import org.junit.jupiter.api.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PdfGeneratorServiceTest {

    private PdfGeneratorService service;
    private PaymentInvoiceRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(PaymentInvoiceRepository.class);
        service = new PdfGeneratorService(repository); // constructor alternativo para inyectar mock
    }

    @Test
    @DisplayName("Should throw exception when invoice not found")
    void generarComprobanteInvoiceNotFoundTest() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(null);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.generarComprobante(id)
        );
        assertTrue(ex.getMessage().contains("No se encontró el pago"));
    }

    @Test
    @DisplayName("Should generate valid PDF with complete invoice data")
    void generarComprobanteValidDataTest() throws Exception {
        UUID id = UUID.randomUUID();
        PaymentInvoice invoice = buildSampleInvoice(id);
        when(repository.findById(id)).thenReturn(invoice);

        byte[] pdfBytes = service.generarComprobante(id);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 200); // asegura que no está vacío, tamaño muy chico para testing
        assertEquals("%PDF-", new String(pdfBytes, 0, 5, StandardCharsets.US_ASCII));

        String pdfText = extractTextFromPdf(pdfBytes);

        assertTrue(pdfText.contains("Código:"), "El PDF debe contener un campo Código");
        assertTrue(pdfText.contains("Cliente: Juan Pérez"));
        assertTrue(pdfText.contains("Monto: USD 1,500.50"));
        assertTrue(pdfText.contains("Estado: PAGADO"));
        assertTrue(pdfText.contains("Detalles del servicio:"));
        assertTrue(pdfText.contains("ProveedorX"));
        assertTrue(pdfText.contains("Referencia: 123456"));
        assertTrue(pdfText.contains("Teléfono: +54 123456789"));
    }

    @Test
    @DisplayName("Should include university/bank name in PDF header")
    void generarComprobanteHeaderTest() throws Exception {
        UUID id = UUID.randomUUID();
        PaymentInvoice invoice = buildSampleInvoice(id);
        when(repository.findById(id)).thenReturn(invoice);

        byte[] pdfBytes = service.generarComprobante(id);
        String pdfText = extractTextFromPdf(pdfBytes);

        assertTrue(pdfText.contains("JALA UNIVERSITY"),
                "PDF should include university name in header");
    }

    @Test
    @DisplayName("Should format amount in Dollar currency")
    void generarComprobanteAmountFormatTest() throws Exception {
        UUID id = UUID.randomUUID();
        PaymentInvoice invoice = buildSampleInvoice(id);
        invoice.setAmount(2500.75);
        when(repository.findById(id)).thenReturn(invoice);

        byte[] pdfBytes = service.generarComprobante(id);
        String pdfText = extractTextFromPdf(pdfBytes);

        assertTrue(pdfText.contains("Monto: USD 2,500.75"),
                "Amount must be formatted as USD 2,500.75");
    }

    private PaymentInvoice buildSampleInvoice(UUID id) {
        Customer customer = new Customer();
        customer.setName("Juan Pérez");

        ExternalService service = new ExternalService();
        service.setProviderName("ProveedorX");
        service.setAccountReference("123456");
        service.setPhoneCountryCode("+54");
        service.setPhoneNumber("123456789");

        PaymentInvoice invoice = new PaymentInvoice();
        invoice.setId(id);
        invoice.setCustomer(customer);
        invoice.setExternalService(service);
        invoice.setAmount(1500.50);
        invoice.setPaymentDate(LocalDateTime.of(2025, 11, 16, 10, 30));

        return invoice;
    }

    private String extractTextFromPdf(byte[] pdfBytes) throws IOException {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document).replaceAll("\\s+", " ").trim();
        }
    }
}
