package org.jala.university.application.service;

import lombok.RequiredArgsConstructor;
import org.jala.university.application.dto.InvoiceDto;
import org.jala.university.application.mapper.InvoiceMapper;
import org.jala.university.application.ports.InvoicesAPI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExternalApiServiceImpl implements ExternalApiService {

    private final InvoicesAPI api;
    private final InvoiceMapper mapper;

    /**
     * Get the invoices from an external service by its client code  and service code.
     * @param clientCode The client user to search for.
     * @param serviceCode The service code to search for.
     * @return A list of invoiceDto.
     */
    @Override
    public List<InvoiceDto> getInvoices(String clientCode, String serviceCode) throws IllegalArgumentException {

        JSONArray invoices = api.getInvoices(clientCode, serviceCode);

        if (invoices == null || invoices.isEmpty()) {
            return List.of();
        }

        List<InvoiceDto> dtos = new ArrayList<>();
        for (Object invoice : invoices) {
            JSONObject invoiceJson = (JSONObject) invoice;
            String status = invoiceJson.get("status").toString().toLowerCase();
            if (!status.equals("paid")) {
                dtos.add(mapper.mapTo(invoiceJson));
            }
        }
        return dtos;
    }

    /**
     * Update the state of an invoice to "paid".
     * @param invoiceCode code of invoice to search for.
     * @param serviceCode service to which the invoice belongs
     * @return true if the invoice was found. false if the invoice was not found.
     */
    @Override
    public boolean updateStatus(String invoiceCode, String serviceCode) {
        try {
            return api.updateStatus(invoiceCode, serviceCode);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
