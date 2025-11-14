package org.jala.university.application.mapper;

import org.jala.university.application.dto.InvoiceDto;
import org.jala.university.commons.application.mapper.Mapper;
import org.json.simple.JSONObject;



public final class InvoiceMapper implements Mapper<JSONObject, InvoiceDto> {

    @Override
    public InvoiceDto mapTo(JSONObject jsonObject) {
        return InvoiceDto.builder()
                .code((String) jsonObject.get("code"))
                .description((String) jsonObject.get("description"))
                .amount((Double) jsonObject.get("amount"))
                .clientName((String) jsonObject.get("client_name"))
                .status((String) jsonObject.get("status"))
                .expeditionDate((String) jsonObject.get("expedition_date"))
                .expirationDate((String) jsonObject.get("expiration_date"))
                .build();
    }

    @Override
    public JSONObject mapFrom(InvoiceDto invoiceDto) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", invoiceDto.getCode());
        jsonObject.put("description", invoiceDto.getDescription());
        jsonObject.put("amount", invoiceDto.getAmount());
        jsonObject.put("client_name", invoiceDto.getClientName());
        jsonObject.put("status", invoiceDto.getStatus());
        jsonObject.put("expedition_date", invoiceDto.getExpeditionDate());
        jsonObject.put("expiration_date", invoiceDto.getExpirationDate());
        return jsonObject;
    }
}
