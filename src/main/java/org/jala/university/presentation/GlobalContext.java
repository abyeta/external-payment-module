package org.jala.university.presentation;

import lombok.Data;
import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.application.dto.InvoiceDto;

import java.util.List;

@Data
public final class GlobalContext {

    private ExternalServiceDto externalService;
    private List<InvoiceDto> invoices;
    private InvoiceDto invoice;
    private String userCode;
    private static final GlobalContext INSTANCE = new GlobalContext();

    public static GlobalContext getInstance() {
        return INSTANCE;
    }

    private GlobalContext() {

    }
}
