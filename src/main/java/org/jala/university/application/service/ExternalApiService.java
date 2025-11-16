package org.jala.university.application.service;

import org.jala.university.application.dto.InvoiceDto;

import java.util.List;

public interface ExternalApiService {

    List<InvoiceDto>  getInvoices(String clientCode, String serviceCode) throws IllegalArgumentException;
    boolean updateStatus(String invoiceCode, String serviceCode);

}
