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

    @Override
    public List<InvoiceDto> getInvoices(String clientCode, String serviceCode) {

        JSONArray invoices = api.getInvoices(clientCode, serviceCode);

        if(invoices == null || invoices.isEmpty()) {
            return List.of();
        }

        List<InvoiceDto> dtos = new ArrayList<>();
        for (Object invoice : invoices) {
            JSONObject invoiceJson = (JSONObject) invoice;
            if(!invoiceJson.equals("paid")){
                dtos.add(mapper.mapTo(invoiceJson));
            }
        }
        return dtos;
    }

    @Override
    public boolean updateStatus(String invoiceCode, String serviceCode) {
        try {
            return api.updateStatus(invoiceCode, serviceCode);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
