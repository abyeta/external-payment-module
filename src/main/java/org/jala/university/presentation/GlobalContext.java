package org.jala.university.presentation;

import lombok.Data;
import org.jala.university.application.dto.CustomerDto;
import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.infrastructure.external.dto.invoice.InvoiceResponse;
import org.jala.university.application.dto.PaymentInvoiceDto;

import java.util.List;

@Data
public final class GlobalContext {

    private ExternalServiceDto externalService;
    private CustomerDto customer;
    private PaymentInvoiceDto paymentInvoice;
    private List<InvoiceResponse> invoices;
    private InvoiceResponse invoice;
    private String userCode;
    private static final GlobalContext INSTANCE = new GlobalContext();

    public static GlobalContext getInstance() {
        return INSTANCE;
    }

    private GlobalContext() {

    }
}
