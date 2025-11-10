package org.jala.university.application.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class InvoiceDto {

    String code;
    String expeditionDate;
    String expirationDate;
    String clientName;
    String status;
    String description;
    double amount;

}
