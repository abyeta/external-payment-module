package org.jala.university.application.mapper;

import org.jala.university.application.dto.InvoiceDto;
import org.jala.university.commons.application.mapper.Mapper;
import org.json.simple.JSONObject;

import java.io.IOException;


public class InvoiceMapper implements Mapper<JSONObject, InvoiceDto> {

    @Override
    public InvoiceDto mapTo(JSONObject jsonObject) {
        return InvoiceDto.builder()
                .code((String) jsonObject.get("code"))
                .description((String) jsonObject.get("description"))
                .amount((Double) jsonObject.get("amount"))
                .clientName((String) jsonObject.get("clientName"))
                .status((String) jsonObject.get("status"))
                .expeditionDate((String) jsonObject.get("expeditionDate"))
                .expirationDate((String) jsonObject.get("expirationDate"))
                .build();
    }

    @Override
    public JSONObject mapFrom(InvoiceDto invoiceDto) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", invoiceDto.getCode());
        jsonObject.put("description", invoiceDto.getDescription());
        jsonObject.put("amount", invoiceDto.getAmount());
        jsonObject.put("clientName", invoiceDto.getClientName());
        jsonObject.put("status", invoiceDto.getStatus());
        jsonObject.put("expeditionDate", invoiceDto.getExpeditionDate());
        jsonObject.put("expirationDate", invoiceDto.getExpirationDate());
        return jsonObject;
    }
}
